package com.spot.good2travel.repository;

import com.spot.good2travel.domain.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    Optional<List<Alarm>> findByUserId(Long userId);
}
