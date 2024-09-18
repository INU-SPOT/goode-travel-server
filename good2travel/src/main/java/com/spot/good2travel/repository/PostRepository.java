package com.spot.good2travel.repository;

import com.spot.good2travel.domain.Post;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAll(Pageable pageable);

    @Query("SELECT p FROM Post p LEFT JOIN FETCH p.itemPosts WHERE p.id = :postId")
    Optional<Post> findPostWithItemsById(@Param("postId") Long postId);

}
