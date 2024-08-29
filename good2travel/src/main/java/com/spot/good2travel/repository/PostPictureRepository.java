package com.spot.good2travel.repository;

import com.spot.good2travel.domain.PostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PostPictureRepository extends JpaRepository<PostImage, Long> {
}
