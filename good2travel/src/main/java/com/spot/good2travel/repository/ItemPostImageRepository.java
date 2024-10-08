package com.spot.good2travel.repository;

import com.spot.good2travel.domain.ItemPostImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemPostImageRepository extends JpaRepository<ItemPostImage, Long> {
}
