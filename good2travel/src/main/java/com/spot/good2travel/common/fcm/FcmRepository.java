package com.spot.good2travel.common.fcm;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FcmRepository extends JpaRepository<Fcm, Long> {

    Fcm findFcmByUserId(Long userId);
}
