package com.spot.good2travel.service;

import com.spot.good2travel.common.exception.ExceptionMessage;
import com.spot.good2travel.common.exception.FailedTokenCreateException;
import com.spot.good2travel.common.exception.NotFoundElementException;
import com.spot.good2travel.common.redisRepository.RefreshToken;
import com.spot.good2travel.common.redisRepository.RefreshTokenRepository;
import com.spot.good2travel.common.security.JwtProvider;
import com.spot.good2travel.domain.User;
import com.spot.good2travel.dto.TokenResponse;
import com.spot.good2travel.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final JwtProvider jwtProvider;

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    //accessToken 재발급과 동시에 refreshToken 도 새로 발급한다.(유효시간을 늘리기 위함.)
    public TokenResponse reissueAccessToken(HttpServletRequest request) {

        String token = jwtProvider.resolveServiceToken(request);

        Long userId = jwtProvider.getMemberId(token);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_NOT_FOUND));

        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId);

        //refreshToken 의 유효 시간과, Header 에 담겨 온 RefreshToken 과 redis 에 저장되어있는 RefreshToken 과 일치하는지 비교한다.

        if (!refreshToken.getRefreshToken().equals(token)) {
            throw new FailedTokenCreateException(ExceptionMessage.TOKEN_EXPIRED);
        }

        String accessToken = jwtProvider.createAccessToken(userId, user.getEmail(), user.getRole());

        String newRefreshToken = jwtProvider.createRefreshToken(userId, user.getEmail(), user.getRole());

        TokenResponse tokenResponse = TokenResponse.builder()
                .refreshToken(newRefreshToken)
                .accessToken(accessToken)
                .build();

        //redis 에 토큰 저장
        refreshTokenRepository.save(new RefreshToken(userId, newRefreshToken));

        return tokenResponse;
    }


}