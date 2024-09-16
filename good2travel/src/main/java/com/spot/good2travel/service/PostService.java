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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.spot.good2travel.dto.PostRequest.PostCreateUpdateRequest;
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
        createGoodAndVisit(post.getId(), 0, 0);
        return post.getId();
    }

    public Long createItemPost(PostRequest.ItemPostCreateUpdateRequest itemPostCreateUpdateRequest, Post post) {
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

    public void createGoodAndVisit(Long postId, Integer visitNum, Integer goodNum) {
        String key = "postId:" + postId;

        redisTemplate.opsForHash().put(key, "visitNum", visitNum);
        redisTemplate.opsForHash().put(key, "goodNum", goodNum);
    }

    @Transactional
    public PostResponse.PostDetailResponse getPost(Long postId, UserDetails userDetails) {

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.POST_NOT_FOUND));

        Long visitNum = updateVisitNum(postId, userDetails);
        Integer goodNum = getGoodNum(postId);
        Boolean isPushGood = getIsPushGood(postId, userDetails);
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
                })
                .toList();

        return PostResponse.PostDetailResponse.of(post, visitNum, writerImageUrl, goodNum, isPushGood, isOwner,itemPostResponses);
    }

    @Transactional
    public CommonPagingResponse<?> getPosts(Integer page, Integer size){
        PageRequest pageable = PageRequest.of(page, size);

        Page<Post> postPage = postRepository.findAll(pageable);

        List<PostResponse.PostThumbnailResponse> postThumbnailResponses = postPage.stream()
                .map(post -> {

                    Long commentNum = null; //아직 로직없음
                    Integer goodNum = getGoodNum(post.getId());

                    String imageUrl = imageService.getImageUrl(itemPostRepository.findById(post.getSequence().get(0))
                            .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.ITEM_POST_NOT_FOUND)).getItem().getImageUrl());
                    return PostResponse.PostThumbnailResponse.of(post, goodNum, commentNum, imageUrl, post.getSequence().stream().map(num -> {
                                ItemPost itemPost = itemPostRepository.findById(num)
                                        .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.ITEM_POST_NOT_FOUND));
                                return PostResponse.ItemPostThumbnailResponse.of(itemPost);
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
            return post.getUser().getId() != userId;
        }

        return null;
    }

    // 특정 postId에 해당하는 goodNum과 visitNum을 업데이트하는 메서드
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

                return redisTemplate.opsForHash().increment(postVisitNumKey, "visitNum", 1);
            }
        }
        return redisTemplate.opsForHash().increment(postVisitNumKey, "visitNum", 0);
    }

    public Long updateGoodNum(Long postId, UserDetails userDetails) {
        String postGoodNumKey = "postId:" + postId;

        if(userDetails != null){
            Long userId = ((CustomUserDetails) userDetails).getId();
            String userVisitKey = "user:" + userId + "goods";

            Boolean hasGood = redisTemplate.opsForSet().isMember(userVisitKey, postId);

            if (Boolean.FALSE.equals(hasGood)) {

                redisTemplate.opsForHash().put(userVisitKey, postId, "1");

                return redisTemplate.opsForHash().increment(postGoodNumKey, "goodNum", 1);
            }
            else if(Boolean.TRUE.equals(hasGood)){

                redisTemplate.opsForHash().delete(userVisitKey, postId);

                return redisTemplate.opsForHash().increment(postGoodNumKey, "goodNum", -1);
            }
        }

        throw new NotFoundElementException(ExceptionMessage.TOKEN_NOT_FOUND);
    }

    public Integer getGoodNum(Long postId) {
        String postVisitNumKey = "postId:" + postId;

        return (Integer) redisTemplate.opsForHash().get(postVisitNumKey, "goodNum");
    }

    public Boolean getIsPushGood(Long postId, UserDetails userDetails){
        String postGoodNumKey = "postId:" + postId;

        if(userDetails != null){
            Long userId = ((CustomUserDetails) userDetails).getId();
            String userGoodKey = "user:" + userId + "goods";

            return redisTemplate.opsForSet().isMember(userGoodKey, postId);
        }
        return null;
    }


}



