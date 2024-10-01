package com.spot.good2travel.service;

import com.spot.good2travel.common.dto.CommonPagingResponse;
import com.spot.good2travel.common.exception.ExceptionMessage;
import com.spot.good2travel.common.exception.NotFoundElementException;
import com.spot.good2travel.common.security.CustomUserDetails;
import com.spot.good2travel.domain.*;
import com.spot.good2travel.dto.PostRequest;
import com.spot.good2travel.dto.PostResponse;
import com.spot.good2travel.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.spot.good2travel.dto.PostRequest.ItemPostCreateUpdateRequest;
import static com.spot.good2travel.dto.PostRequest.PostCreateUpdateRequest;
import static com.spot.good2travel.dto.PostResponse.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ItemPostRepository itemPostRepository;
    private final ItemRepository itemRepository;
    private final ItemPostImageRepository itemPostImageRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CommentRepository commentRepository;
    private final ReplyCommentRepository replyCommentRepository;
    private final ItemService itemService;
    private final ImageService imageService;


    @Transactional
    public Long createPost(PostCreateUpdateRequest postCreateUpdateRequest, UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.USER_NOT_FOUND));

        Post post = Post.of(postCreateUpdateRequest, user);

        postRepository.save(post);

        List<Long> sequence = postCreateUpdateRequest.getItemPosts().stream()
                .map(itemPostCreateUpdateRequest -> createItemPost(itemPostCreateUpdateRequest, post))
                .toList();

        postRepository.save(post);

        post.updatePostSequence(sequence);
        createLikeAndVisit(post.getId(), 0, 0);

        return post.getId();
    }

    @Transactional
    public Long createItemPost(ItemPostCreateUpdateRequest itemPostCreateUpdateRequest, Post post) {
        Long itemId = itemPostCreateUpdateRequest.getItemId();

        Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.ITEM_NOT_FOUND));

        ItemPost itemPost = ItemPost.of(itemPostCreateUpdateRequest, item, post);
        itemPostRepository.save(itemPost);

        List<Long> sequence = itemPostCreateUpdateRequest.getImages().stream()
                .map(image -> {
                    if(post.getThumbnailImageName() == null && image.getImageName() != null){
                        post.updatePostImageName(image.getImageName());
                    }
                    return createItemPostImage(image, itemPost);
                })
                .toList();

        itemPost.updateSequence(sequence);

        return itemPost.getId();
    }

    @Transactional
    public Long createItemPostImage(PostRequest.ItemPostImageRequest itemPostImageRequest, ItemPost itemPost){
        ItemPostImage itemPostImage = ItemPostImage.of(itemPostImageRequest, itemPost);
        itemPostImageRepository.save(itemPostImage);
        return itemPostImage.getId();
    }

    @Transactional
    public void createLikeAndVisit(Long postId, Integer visitNum, Integer likeNum) {
        String key = "postId:" + postId;

        redisTemplate.opsForHash().put(key, "visitNum", visitNum);
        redisTemplate.opsForHash().put(key, "likeNum", likeNum);
    }

    @Transactional
    public void deleteLikeAndVisit(Long postId) {
        String key = "postId:" + postId;

        redisTemplate.delete(key);
        redisTemplate.opsForZSet().remove("postLikes", postId);
        redisTemplate.opsForZSet().remove("postVisits", postId);
    }

    @Transactional
    public PostResponse.PostDetailResponse getPost(Long postId, UserDetails userDetails) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.POST_NOT_FOUND));

        Long visitNum = updateVisitNum(postId, userDetails);
        Integer likeNum = getLikeNum(postId);
        Boolean isPushLike = getIsPushLike(postId, userDetails);
        Boolean isOwner = validateUserIsPostOwner(post, userDetails);
        Long commentNum = getTotalComments(postId);

        String writerImageUrl = post.getUser().getProfileImageName() != null ? post.getUser().getProfileImageName() : imageService.getDefaultUserImageName();

        List<PostResponse.ItemPostResponse> itemPostResponses = post.getSequence().stream()
                .map(num -> {
                    ItemPost itemPost = itemPostRepository.findById(num)
                            .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.ITEM_POST_NOT_FOUND));

                    List<PostResponse.ItemPostImageResponse> itemPostImageResponses = itemPost.getItemPostImages().stream()
                            .map( image -> PostResponse.ItemPostImageResponse.of(image.getId(), image.getImageName()))
                            .toList();

                    return PostResponse.ItemPostResponse.of(itemPost, itemPostImageResponses);
                }).toList();

        return PostDetailResponse.of(post, visitNum, writerImageUrl, likeNum, commentNum, isPushLike, isOwner,itemPostResponses);
    }

    @Transactional
    public Long getTotalComments(Long postId) {
        Long commentCount = commentRepository.countCommentsByPostId(postId);
        Long replyCount = replyCommentRepository.countRepliesByPostId(postId);

        return commentCount + replyCount;
    }

    @Transactional
    public Long updatePost(Long postId, PostCreateUpdateRequest postCreateUpdateRequest, UserDetails userDetails) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.POST_NOT_FOUND));

        validateUserIsPostOwner(post, userDetails);

        List<Long> beforeSequence = post.getSequence();
        List<Long> afterSequence = postCreateUpdateRequest.getItemPosts().stream()
                .map(itemPostUpdateRequest -> updateItemPost(itemPostUpdateRequest, post))
                .toList();

        Set<Long> afterSequenceSet = new HashSet<>(afterSequence);
        beforeSequence.stream()
                .filter(num -> !afterSequenceSet.contains(num))
                .forEach(this::deleteItemPost);

        post.updatePost(postCreateUpdateRequest, findPostThumbnailImage(post), afterSequence);
        return postId;
    }

    @Transactional
    public Long updateItemPost(PostRequest.ItemPostCreateUpdateRequest itemPostCreateUpdateRequest, Post post) {
        Long itemId = itemPostCreateUpdateRequest.getItemId();

        if (itemPostCreateUpdateRequest.getItemPostId() != null) {
            Item item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.ITEM_NOT_FOUND));

            ItemPost itemPost = itemPostRepository.findById(itemPostCreateUpdateRequest.getItemPostId())
                    .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.ITEM_POST_NOT_FOUND));
            List<Long> beforeSequence = itemPost.getSequence();

            List<Long> afterSequence = itemPostCreateUpdateRequest.getImages().stream()
                    .map(image -> updateItemPostImage(image, itemPost))
                    .toList();

            itemPost.updateItemPost(itemPostCreateUpdateRequest, afterSequence, item, post);

            Set<Long> afterSequenceSet = new HashSet<>(afterSequence);
            log.info(beforeSequence.toString());

            beforeSequence.stream()
                    .filter(num -> !afterSequenceSet.contains(num))
                    .forEach(this::deleteItemPostImage);

            return itemPost.getId();
        }
        else {
            return createItemPost(itemPostCreateUpdateRequest, post);
        }
    }

    @Transactional
    public Long updateItemPostImage(PostRequest.ItemPostImageRequest itemPostImageRequest, ItemPost itemPost){
        if(itemPostImageRequest.getItemPostImageId() != null){

            ItemPostImage itemPostImage = itemPostImageRepository.findById(itemPostImageRequest.getItemPostImageId())
                    .orElseThrow(()->new NotFoundElementException(ExceptionMessage.ITEM_POST_IMAGE_NOT_FOUND));
            itemPostImage.updateItemPostImage(itemPostImageRequest);
            itemPostRepository.save(itemPost);
            return itemPostImage.getId();
        }
        else{
            return createItemPostImage(itemPostImageRequest, itemPost);
        }
    }

    @Transactional
    public void deleteItemPost(Long itemPostId) {

        ItemPost itemPost = itemPostRepository.findById(itemPostId)
                .orElseThrow(()-> new NotFoundElementException(ExceptionMessage.ITEM_POST_NOT_FOUND));

        itemPostRepository.delete(itemPost);
    }

    @Transactional
    public void deleteItemPostImage(Long itemPostImageId){
        ItemPostImage itemPostImage = itemPostImageRepository.findById(itemPostImageId)
                .orElseThrow(()-> new NotFoundElementException(ExceptionMessage.ITEM_POST_IMAGE_NOT_FOUND));

        itemPostImageRepository.delete(itemPostImage);
    }

    @Transactional
    public Boolean validateUserIsPostOwner(Post post, UserDetails userDetails){
        if(userDetails != null){
            Long userId = ((CustomUserDetails) userDetails).getId();
            return Objects.equals(post.getUser().getId(), userId);
        }

        return false;
    }

    @Transactional
    public Long updateVisitNum(Long postId, UserDetails userDetails) {
        String postVisitNumKey = "postId:" + postId;

        if(userDetails != null){
            Long userId = ((CustomUserDetails) userDetails).getId();
            String userVisitKey = "user:" + userId + "visits";

            Boolean hasVisited = redisTemplate.opsForSet().isMember(userVisitKey, postId);

            if (Boolean.FALSE.equals(hasVisited)) {

                redisTemplate.opsForSet().add(userVisitKey, postId);

                redisTemplate.expire(userVisitKey, 24, TimeUnit.HOURS);

            }
        }
        Long updateVisitNum = redisTemplate.opsForHash().increment(postVisitNumKey, "visitNum", 1);
        redisTemplate.opsForZSet().add("postVisits", postId, updateVisitNum);
        return updateVisitNum;
    }

    @Transactional
    public Long updateLikeNum(Long postId, UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();
        String userLikeKey = "user:" + userId + "likes";
        String postLikeNumKey = "postId:" + postId;

        Boolean hasLike = redisTemplate.opsForSet().isMember(userLikeKey, postId);

        if (Boolean.FALSE.equals(hasLike)) {
            redisTemplate.opsForSet().add(userLikeKey, postId);
            Long updatedLikeNum = redisTemplate.opsForHash().increment(postLikeNumKey, "likeNum", 1);
            redisTemplate.opsForZSet().add("postLikes", postId, updatedLikeNum);

            return updatedLikeNum;
        } else if (Boolean.TRUE.equals(hasLike)) {
            redisTemplate.opsForSet().remove(userLikeKey, postId);
            Long updatedLikeNum = redisTemplate.opsForHash().increment(postLikeNumKey, "likeNum", -1);
            redisTemplate.opsForZSet().add("postLikes", postId, updatedLikeNum);

            return updatedLikeNum;
        }

        throw new NotFoundElementException(ExceptionMessage.TOKEN_NOT_FOUND);
    }

    @Transactional
    public Integer getLikeNum(Long postId) {
        String postLikeNumKey = "postId:" + postId;

        Integer likeNum = (Integer) redisTemplate.opsForHash().get(postLikeNumKey, "likeNum");

        return likeNum != null ? likeNum : 0;
    }

    @Transactional
    public Boolean getIsPushLike(Long postId, UserDetails userDetails){
        if(userDetails != null){
            Long userId = ((CustomUserDetails) userDetails).getId();
            String userLikeKey = "user:" + userId + "likes";

            return redisTemplate.opsForSet().isMember(userLikeKey, postId);
        }
        return false;
    }

    @Transactional
    public TopPostResponse getTopLikePost() {

        Set<ZSetOperations.TypedTuple<Object>> topPostId = redisTemplate.opsForZSet()
                .reverseRangeWithScores("postLikes", 0, 0);

        if (topPostId == null || topPostId.isEmpty()) {
            throw new NotFoundElementException(ExceptionMessage.POST_NOT_FOUND);
        }

        ZSetOperations.TypedTuple<Object> topPost = topPostId.iterator().next();
        Long postId = Long.valueOf(topPost.getValue().toString());
        Long likeCount = topPost.getScore().longValue();

        Post post = postRepository.findPostWithItemsById(postId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.POST_NOT_FOUND));

        List<ItemPostThumbnailResponse> itemPostThumbnailResponses = post.getItemPosts().stream()
                .map(ItemPostThumbnailResponse::of)
                .toList();

        return TopPostResponse.of(post, "like", likeCount, itemPostThumbnailResponses);
    }

    @Transactional
    public TopPostResponse getTopVisitPost() {

        Set<ZSetOperations.TypedTuple<Object>> topPostId = redisTemplate.opsForZSet()
                .reverseRangeWithScores("postVisits", 0, 0);

        if (topPostId == null || topPostId.isEmpty()) {
            throw new NotFoundElementException(ExceptionMessage.POST_NOT_FOUND);
        }

        ZSetOperations.TypedTuple<Object> topPost = topPostId.iterator().next();
        Long postId = Long.valueOf(topPost.getValue().toString());

        Long viewCount = topPost.getScore().longValue();

        Post post = postRepository.findPostWithItemsById(postId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.POST_NOT_FOUND));

        List<ItemPostThumbnailResponse> itemPostThumbnailResponses = post.getItemPosts().stream()
                .map(ItemPostThumbnailResponse::of)
                .toList();

        return TopPostResponse.of(post, "visit", viewCount, itemPostThumbnailResponses);
    }

    @Transactional
    public CommonPagingResponse<?> getUserPosts(Integer page, Integer size, UserDetails userDetails){
        Long userId = ((CustomUserDetails) userDetails).getId();

        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate"));

        Page<Post> postPage = postRepository.findPostsByUserId(userId, pageable);

        List<PostResponse.PostThumbnailResponse> postThumbnailResponses = getPostThumbnails(postPage);

        return new CommonPagingResponse<>(page, size, postPage.getTotalElements(), postPage.getTotalPages(), postThumbnailResponses);
    }

    @Transactional
    public CommonPagingResponse<?> getUserLikePosts(Integer page, Integer size, UserDetails userDetails){
        Long userId = ((CustomUserDetails) userDetails).getId();
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate"));
        String userLikeKey = "user:" + userId + "likes";

        Set<Object> likePosts = redisTemplate.opsForSet().members(userLikeKey);

        List<Long> likePostsNum = Objects.requireNonNull(likePosts).stream()
                .map(id -> Long.parseLong(id.toString()))
                .toList();

        Page<Post> postPage = postRepository.findAllByIdIn(likePostsNum, pageable);
        return new CommonPagingResponse<>(page, size, postPage.getTotalElements(), postPage.getTotalPages(), getPostThumbnails(postPage));
    }


    @Transactional
    public String findPostThumbnailImage(Post post){
        if(post.getItemPosts() == null){
            return null;
        }
        else{
            return post.getItemPosts().stream()
                    .flatMap(itemPost -> itemPost.getItemPostImages().stream())
                    .map(ItemPostImage::getImageName)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }
    }

    @Transactional
    public List<PostThumbnailResponse> getPostThumbnails(Page<Post> posts){
        return posts.stream()
                .map(post -> {
                    Long commentNum = getTotalComments(post.getId());
                    Integer likeNum = getLikeNum(post.getId());

                    String imageName = post.getThumbnailImageName() != null ? post.getThumbnailImageName() : imageService.getDefaultImageName();

                    return PostResponse.PostThumbnailResponse.of(post, likeNum, commentNum, imageName, post.getSequence().stream().map(num -> {
                        ItemPost itemPost = itemPostRepository.findById(num)
                                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.ITEM_POST_NOT_FOUND));
                        return ItemPostThumbnailResponse.of(itemPost);
                    }).toList());
                }).toList();
    }

    @Transactional
    public CommonPagingResponse<?> searchPosts(List<Long> metropolitanGovernments, List<Long> localGovernments, List<String> categories, String keyword, Integer page, Integer size) {
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate"));

        boolean hasNoConditions = (metropolitanGovernments == null || metropolitanGovernments.isEmpty()) &&
                (localGovernments == null || localGovernments.isEmpty()) &&
                (categories == null || categories.isEmpty()) &&
                (keyword == null || keyword.trim().isEmpty());

        Page<Post> postPage;
        if (hasNoConditions) {
            postPage = postRepository.findAll(pageable);
        } else {
            postPage = postRepository.searchPostsByCriteria(metropolitanGovernments, localGovernments, categories, keyword, pageable);
        }

        return new CommonPagingResponse<>(page, size, postPage.getTotalElements(), postPage.getTotalPages(), getPostThumbnails(postPage));
    }

    @Transactional
    public void deletePost(Long postId, UserDetails userDetails) {
        Post post = postRepository.findById(postId)
                .orElseThrow(()-> new NotFoundElementException(ExceptionMessage.POST_NOT_FOUND));

        validateUserIsPostOwner(post, userDetails);
        deleteLikeAndVisit(post.getId());

        post.getItemPosts()
                .forEach(num -> itemService.deleteUserItem(num.getItem()));

        postRepository.delete(post);
    }

}



