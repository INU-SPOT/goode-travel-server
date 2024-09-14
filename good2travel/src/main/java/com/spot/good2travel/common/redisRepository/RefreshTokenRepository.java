package com.spot.good2travel.common.redisRepository;

import org.springframework.data.repository.CrudRepository;

public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String> {

    RefreshToken findByUserId(Long userId);
}