package com.spot.good2travel.service;

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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    @Transactional
    public PostResponse.PostDetailResponse getPost(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.POST_NOT_FOUND));

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

        return PostResponse.PostDetailResponse.of(post, itemPostResponses);
    }

    @Transactional
    public List<PostResponse.PostThumbnailResponse> getPosts(Integer page, Integer size){
        PageRequest pageable = PageRequest.of(page, size);

        Page<Post> postPage = postRepository.findAll(pageable);

        List<PostResponse.PostThumbnailResponse> postThumbnailResponses = postPage.stream()
                .map(post -> {
                    String imageUrl = imageService.getImageUrl(itemPostRepository.findById(post.getSequence().get(0))
                            .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.ITEM_POST_NOT_FOUND)).getItem().getImageUrl());
                    return PostResponse.PostThumbnailResponse.of(post, imageUrl, post.getSequence().stream().map(num -> {
                                ItemPost itemPost = itemPostRepository.findById(num)
                                        .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.ITEM_POST_NOT_FOUND));
                                return PostResponse.ItemPostThumbnailResponse.of(itemPost);
                    }).toList());
                }).toList();

        return postThumbnailResponses;
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

    public void validateUserIsPostOwner(Post post, UserDetails userDetails){
        Long userId = ((CustomUserDetails) userDetails).getId();

        if(post.getUser().getId() != userId){
            throw new RuntimeException(ExceptionMessage.MEMBER_UNAUTHENTICATED.getMessage());
        }
    }

}



