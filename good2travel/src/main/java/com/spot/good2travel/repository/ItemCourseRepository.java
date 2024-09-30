package com.spot.good2travel.repository;

import com.spot.good2travel.domain.ItemCourse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemCourseRepository extends JpaRepository<ItemCourse, Long> {
}
