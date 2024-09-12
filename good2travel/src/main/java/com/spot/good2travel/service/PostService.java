package com.spot.good2travel.service;

import com.spot.good2travel.repository.ItemPostRepository;
import com.spot.good2travel.repository.ItemRepository;
import com.spot.good2travel.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final ItemPostRepository itemPostRepository;
    private final ItemRepository itemRepository;



}
