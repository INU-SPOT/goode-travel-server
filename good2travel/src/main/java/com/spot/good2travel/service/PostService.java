package com.spot.good2travel.service;

import com.spot.good2travel.common.dto.CommonPagingResponse;
import com.spot.good2travel.common.exception.ExceptionMessage;
import com.spot.good2travel.common.exception.NotFoundElementException;
import com.spot.good2travel.common.security.CustomUserDetails;
import com.spot.good2travel.domain.*;
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

import java.util.List;
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
    private final ImageService imageService;
    private final RedisTemplate<String, Object> redisTemplate;

    @Transactional
    public Long createPost(PostCreateUpdateRequest postCreateUpdateRequest, UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_NOT_FOUND));

        Post post = Post.of(postCreateUpdateRequest, user);

        List<Long> sequence = postCreateUpdateRequest.getItemPosts().stream()
                .map(itemPostCreateUpdateRequest -> createItemPost(itemPostCreateUpdateRequest, post))
                .toList();

        post.updatePostSequence(sequence);
        postRepository.save(post);
        createLikeAndVisit(post.getId(), 0, 0);
        return post.getId();
    }

    public Long createItemPost(ItemPostCreateUpdateRequest itemPostCreateUpdateRequest, Post post) {
        Long itemId = itemPostCreateUpdateRequest.getItemId();
        Item item;
        if (itemId != null) {
            item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.ITEM_NOT_FOUND));
        } else {
            //item 만드는 로직이 아직 없습니다.
            item = null;
        }
        ItemPost itemPost = ItemPost.of(itemPostCreateUpdateRequest, item, post);
        itemPostRepository.save(itemPost);

        List<ItemPostImage> itemPostImages = itemPostCreateUpdateRequest.getImages().stream()
                .map(itemPostImageRequest -> ItemPostImage.of(itemPostImageRequest, itemPost)).toList();
        itemPostImageRepository.saveAll(itemPostImages);

        return itemPost.getId();
    }

    public void createLikeAndVisit(Long postId, Integer visitNum, Integer likeNum) {
        String key = "postId:" + postId;

        redisTemplate.opsForHash().put(key, "visitNum", visitNum);
        redisTemplate.opsForHash().put(key, "likeNum", likeNum);
    }

    @Transactional
    public PostDetailResponse getPost(Long postId, UserDetails userDetails) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.POST_NOT_FOUND));

        Long visitNum = updateVisitNum(postId, userDetails);
        Integer likeNum = getLikeNum(postId);
        Boolean isPushLike = getIsPushLike(postId, userDetails);
        Boolean isOwner = validateUserIsPostOwner(post, userDetails);

        String writerImageUrl = imageService.getImageUrl(post.getUser().getProfileImageName());

        List<PostResponse.ItemPostResponse> itemPostResponses = post.getSequence().stream()
                .map(num -> {
                    ItemPost itemPost = itemPostRepository.findById(num)
                            .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.ITEM_POST_NOT_FOUND));

                    List<PostResponse.ItemPostImageResponse> itemPostImageResponses = itemPost.getItemPostImages().stream()
                            .map( image -> PostResponse.ItemPostImageResponse.of(image.getId(), imageService.getImageUrl(image.getImageUrl())))
                            .toList();

                    return PostResponse.ItemPostResponse.of(itemPost, itemPostImageResponses);
                }).toList();

        return PostDetailResponse.of(post, visitNum, writerImageUrl, likeNum, isPushLike, isOwner,itemPostResponses);
    }

    @Transactional
    public CommonPagingResponse<?> getPosts(Integer page, Integer size){
        PageRequest pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createDate"));

        Page<Post> postPage = postRepository.findAll(pageable);

        List<PostResponse.PostThumbnailResponse> postThumbnailResponses = postPage.stream()
                .map(post -> {

                    Long commentNum = null; //아직 로직없음
                    Integer likeNum = getLikeNum(post.getId());

                    String imageUrl = imageService.getImageUrl(itemPostRepository.findById(post.getSequence().get(0))
                            .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.ITEM_POST_NOT_FOUND)).getItem().getImageUrl());

                    return PostResponse.PostThumbnailResponse.of(post, likeNum, commentNum, imageUrl, post.getSequence().stream().map(num -> {
                                ItemPost itemPost = itemPostRepository.findById(num)
                                        .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.ITEM_POST_NOT_FOUND));
                                return ItemPostThumbnailResponse.of(itemPost);
                    }).toList());
                }).toList();

        return new CommonPagingResponse<>(page, size, postPage.getTotalElements(), postPage.getTotalPages(), postThumbnailResponses);
    }

    @Transactional
    public Long updatePost(Long postId, PostCreateUpdateRequest postCreateUpdateRequest, UserDetails userDetails){
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.POST_NOT_FOUND));

        validateUserIsPostOwner(post, userDetails);

        itemPostRepository.deleteAll(itemPostRepository.findItemPostsByPost(post));

        List<Long> beforeSequence = postCreateUpdateRequest.getItemPosts().stream()
                .map(itemPostUpdateRequest -> createItemPost(itemPostUpdateRequest, post))
                .toList();

        post.updatePost(postCreateUpdateRequest, beforeSequence);

        return postId;
    }

    public Boolean validateUserIsPostOwner(Post post, UserDetails userDetails){

        if(userDetails != null){
            Long userId = ((CustomUserDetails) userDetails).getId();
            return post.getUser().getId() == userId;
        }

        return false;
    }

    public Long updateVisitNum(Long postId, UserDetails userDetails) {
        String postVisitNumKey = "postId:" + postId;

        if(userDetails != null){
            Long userId = ((CustomUserDetails) userDetails).getId();
            String userVisitKey = "user:" + userId + "visits";

            Boolean hasVisited = redisTemplate.opsForSet().isMember(userVisitKey, postId);
            log.info(hasVisited.toString());
            if (Boolean.FALSE.equals(hasVisited)) {

                redisTemplate.opsForSet().add(userVisitKey, postId);

                redisTemplate.expire(userVisitKey, 24, TimeUnit.HOURS);

            }
        }
        Long updateVisitNum = redisTemplate.opsForHash().increment(postVisitNumKey, "visitNum", 1);
        redisTemplate.opsForZSet().add("postVisits", postId, updateVisitNum);
        return updateVisitNum;
    }

    public Long updateLikeNum(Long postId, UserDetails userDetails) {
        if (userDetails == null) {
            throw new NotFoundElementException(ExceptionMessage.TOKEN_NOT_FOUND);
        }

        Long userId = ((CustomUserDetails) userDetails).getId();
        String userLikeKey = "user:" + userId + ":likes";
        String postLikeNumKey = "postId:" + postId + ":likeNum";

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


    public Integer getLikeNum(Long postId) {
        String postVisitNumKey = "postId:" + postId;

        return (Integer) redisTemplate.opsForHash().get(postVisitNumKey, "likeNum");
    }

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


}



