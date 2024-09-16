package com.spot.good2travel.common.security;

import com.spot.good2travel.common.redisRepository.RefreshToken;
import com.spot.good2travel.common.redisRepository.RefreshTokenRepository;
import com.spot.good2travel.domain.User;
import com.spot.good2travel.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtProvider jwtProvider;

    private final UserRepository userRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    @Value("${spring.front-redirect-url.host}")
    private String frontHost;

    @Value("${spring.front-redirect-url.port}")
    private int frontPort;

    @Value("${spring.front-redirect-url.scheme}")
    private String frontScheme;

    @Value("${spring.front-redirect-url.path}")
    private String frontPath;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        DefaultOAuth2User oAuth2User = (DefaultOAuth2User) authentication.getPrincipal();
        log.info(oAuth2User + " " + oAuth2User.getAuthorities());
        User user = userRepository.findByEmail(oAuth2User.getName()).get();

        redirectToken(request, response, user);
    }

    private void redirectToken(HttpServletRequest request, HttpServletResponse response, User user) throws IOException {

        Long memberId = user.getId();
        String email = user.getEmail();

        String accessToken = jwtProvider.createAccessToken(memberId, email, List.of(Role.ROLE_USER.getRole()));
        String refreshToken = jwtProvider.createRefreshToken(memberId, email, List.of(Role.ROLE_USER.getRole()));

        refreshTokenRepository.save(new RefreshToken(memberId, refreshToken));

        String uri = createURI(accessToken, refreshToken, memberId).toString();
        getRedirectStrategy().sendRedirect(request, response, uri);
    }

    private URI createURI(String accessToken, String refreshToken, Long userId) {
        MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
        queryParams.add("accessToken", accessToken);
        queryParams.add("refreshToken", refreshToken);
        log.info("{}", frontHost);
        return UriComponentsBuilder.newInstance()
                .scheme(frontScheme)
                .host(frontHost)
                .port(frontPort)
                .path(frontPath)
                .queryParams(queryParams)
                .build()
                .toUri();
    }
}