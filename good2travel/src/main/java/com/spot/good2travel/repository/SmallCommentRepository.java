package com.spot.good2travel.repository;

import com.spot.good2travel.domain.SmallComment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SmallCommentRepository extends JpaRepository<SmallComment, Long> {
}
