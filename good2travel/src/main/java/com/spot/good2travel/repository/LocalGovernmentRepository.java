package com.spot.good2travel.repository;

import com.spot.good2travel.domain.LocalGovernment;
import jakarta.persistence.Id;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LocalGovernmentRepository extends JpaRepository<LocalGovernment, Long> {
}
