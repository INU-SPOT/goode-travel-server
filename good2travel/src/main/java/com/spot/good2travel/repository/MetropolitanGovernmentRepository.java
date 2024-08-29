package com.spot.good2travel.repository;

import com.spot.good2travel.domain.MetropolitanGovernment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetropolitanGovernmentRepository extends JpaRepository<MetropolitanGovernment, Long> {
}
