package com.spot.good2travel.repository;

import com.spot.good2travel.domain.Course;
import com.spot.good2travel.domain.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    Optional<Course> findByMainItem(Item item);

}
