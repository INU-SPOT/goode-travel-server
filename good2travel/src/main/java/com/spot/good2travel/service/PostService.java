package com.spot.good2travel.service;

import com.spot.good2travel.common.exception.ExceptionMessage;
import com.spot.good2travel.common.exception.NotFoundElementException;
import com.spot.good2travel.common.security.CustomUserDetails;
import com.spot.good2travel.domain.*;
import com.spot.good2travel.dto.PostRequest;
import com.spot.good2travel.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static com.spot.good2travel.dto.PostRequest.PostCreateRequest;
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ItemPostRepository itemPostRepository;
    private final ItemRepository itemRepository;
    private final ItemPostImageRepository itemPostImageRepository;

    public Long createPost(PostCreateRequest postCreateRequest, UserDetails userDetails){
        Long userId = ((CustomUserDetails) userDetails).getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_NOT_FOUND));

        List<Long> sequence = new ArrayList<>();

        Post post = Post.of(postCreateRequest, user);
        postCreateRequest.getItemPosts().stream().map(itemPostCreateRequest -> sequence.add(createItemPost(itemPostCreateRequest, post)));
        post.updatePostSequence(sequence);

        postRepository.save(post);

        return post.getId();
    }

    public Long createItemPost(PostRequest.ItemPostCreateRequest itemPostCreateRequest, Post post){
        Long itemId = itemPostCreateRequest.getItemId();
        Item item;
        if(itemId != null){
            item = itemRepository.findById(itemId)
                    .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_NOT_FOUND));
        }
        else{
            //item 만드는 로직이 아직 없습니다.
            item = null;
        }
        ItemPost itemPost = ItemPost.of(itemPostCreateRequest, item, post);
        itemPostRepository.save(itemPost);

        List<ItemPostImage> itemPostImages= itemPostCreateRequest.getImages().stream()
                .map(itemPostImageRequest -> ItemPostImage.of(itemPostImageRequest, itemPost)).toList();
        itemPostImageRepository.saveAll(itemPostImages);

        return itemPost.getId();
    }

}
