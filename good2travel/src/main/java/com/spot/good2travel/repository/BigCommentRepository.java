package com.spot.good2travel.repository;

import com.spot.good2travel.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BigCommentRepository extends JpaRepository<Comment, Long> {
}
