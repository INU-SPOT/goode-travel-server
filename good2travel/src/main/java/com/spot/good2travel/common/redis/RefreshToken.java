package com.spot.good2travel.common.redis;

import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

@Getter
@RedisHash(value = "refreshToken", timeToLive = 14440)
public class RefreshToken {


    private String refreshToken;

    @Id
    @Indexed
    private Long userId;

    public RefreshToken(Long userId, String refreshToken) {
        this.refreshToken = refreshToken;
        this.userId = userId;
    }
}
