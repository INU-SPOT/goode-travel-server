package com.spot.good2travel.common.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.RequestAttributeSecurityContextRepository;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final RequestAttributeSecurityContextRepository requestAttributeSecurityContextRepository
            = new RequestAttributeSecurityContextRepository();

    @Override
    protected void doFilterInternal(HttpServletRequest servletRequest,
                                    HttpServletResponse servletResponse,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = jwtProvider.getAuthorizationToken(servletRequest);

        log.info("[doFilterInternal] 토큰 얻어오기 성공");

        if (token != null) {
            String jwtToken = token.substring(7);
            log.info("[doFilterInternal] jwtToken:{}", jwtToken);

            if (servletRequest.getRequestURI().equals("/v1/auth/reissue")) {
                jwtProvider.validRefreshToken(jwtToken);
            } else {
                jwtProvider.validAccessToken(jwtToken);
            }
            log.info("[doFilterInternal] 토큰 타입 확인 완료");

            jwtProvider.validDateToken(jwtToken);
            log.info("결과: {}, ", jwtProvider.validDateToken(jwtToken));
            Authentication authentication = jwtProvider.getAuthentication(jwtToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            requestAttributeSecurityContextRepository.saveContext(SecurityContextHolder.getContext(), servletRequest, servletResponse);
            log.info("[doFilterInternal] 토큰 값 검증 완료.git");
        }


        filterChain.doFilter(servletRequest, servletResponse);
    }

}
