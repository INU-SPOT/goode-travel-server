package com.spot.good2travel.repository;

import com.spot.good2travel.domain.ReplyComment;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReplyCommentRepository extends JpaRepository<ReplyComment, Long> {
    @Query("SELECT COUNT(rc) FROM ReplyComment rc WHERE rc.comment.id IN (SELECT c.id FROM Comment c WHERE c.post.id = :postId)")
    Long countRepliesByPostId(@Param("postId") Long postId);

    List<ReplyComment> findByUserId(Long userId);
}
