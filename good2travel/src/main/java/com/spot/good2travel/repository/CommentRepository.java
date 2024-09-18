package com.spot.good2travel.repository;

import com.spot.good2travel.domain.Comment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Query("SELECT c FROM Comment c JOIN FETCH c.post WHERE c.post.id = :postId")
    List<Comment> findAllByPostId(@Param("postId") Long postId);

}
