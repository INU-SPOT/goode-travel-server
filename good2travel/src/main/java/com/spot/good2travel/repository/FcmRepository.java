package com.spot.good2travel.repository;

import com.spot.good2travel.domain.Fcm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FcmRepository extends JpaRepository<Fcm, Long> {

    Fcm findFcmByUserId(Long userId);
}
