package com.spot.good2travel.repository;

import com.spot.good2travel.domain.Fcm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FcmRepository extends JpaRepository<Fcm, Long> {

    Optional<Fcm> findByUserId(Long userId);
}
