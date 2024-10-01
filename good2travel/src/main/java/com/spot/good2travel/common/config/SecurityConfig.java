package com.spot.good2travel.common.config;

import com.spot.good2travel.common.exception.JwtExceptionHandlerFilter;
import com.spot.good2travel.common.security.JwtAuthFilter;
import com.spot.good2travel.common.security.JwtProvider;
import com.spot.good2travel.common.security.OAuth2SuccessHandler;
import com.spot.good2travel.service.CustomOAuth2Service;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.annotation.web.configurers.HttpBasicConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProvider jwtProvider;

    private final AccessDeniedHandler accessDeniedHandler;

    private final AuthenticationEntryPoint authenticationEntryPoint;

    private final CustomOAuth2Service customOAuth2Service;

    private final OAuth2SuccessHandler oAuth2SuccessHandler;

    @Bean
    protected SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .httpBasic(HttpBasicConfigurer::disable)
                .csrf(CsrfConfigurer::disable)
                .cors(security -> security.configurationSource(corsConfigurationSource()))
                .sessionManagement((sessionManagement) ->
                        sessionManagement
                                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers(PERMIT_ALL).permitAll()
                        .requestMatchers(HttpMethod.GET, PERMIT_USER_GET).hasRole("USER")
                        .requestMatchers(HttpMethod.POST, PERMIT_USER_POST).hasRole("USER")
                        .requestMatchers(HttpMethod.DELETE, PERMIT_USER_DELETE).hasRole("USER")
                        .requestMatchers(HttpMethod.PUT, PERMIT_USER_PUT).hasRole("USER")
                        .requestMatchers(HttpMethod.PATCH, PERMIT_USER_PATCH).hasRole("USER")
                        .anyRequest().hasRole("ADMIN")
                )
                .oauth2Login(oauth2 -> oauth2
                        .userInfoEndpoint(userInfoEndpointConfig -> userInfoEndpointConfig
                                .userService(customOAuth2Service)
                        )
                        .successHandler(oAuth2SuccessHandler))
                .addFilterBefore(new JwtExceptionHandlerFilter(),
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new JwtAuthFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class)

                .exceptionHandling(httpSecurityExceptionHandlingConfigurer -> httpSecurityExceptionHandlingConfigurer
                        .accessDeniedHandler(accessDeniedHandler)
                        .authenticationEntryPoint(authenticationEntryPoint)
                );
        return httpSecurity.build();
    }

        String[] PERMIT_ALL = {
            "/oauth2/**", //oauth2 로그인 서비스 접근
            "/login/**", //oauth2 로그인창
            "/swagger-ui/**", //스웨거 명세
            "/v3/api-docs/**", //스웨거 명세
            "/v1/users/image",
            "/v1/posts/{postid}/comments",
            "/v1/items/{itemId}",
            "/v1/items",
            "/v1/items/{itemid}/weather",
            "/v1/items/recommend",
            "/v1/items/random",
            "/v1/item/{itemid}/course",
            "/v1/posts",
            "/v1/posts/{postid}",
            "/v1/posts/top-visit",
            "/v1/posts/top-like",
                "/v1/users/is-registered",
                "/v1/fcm",
                "/v1/fcm/send"
    };

    String[] PERMIT_USER_GET = {
            "/v1/users",
            "/v1/users/comments",
            "/v1/folders",
            "/v1/folders/{folderId}",
            "/v1/users/posts",
            "/v1/users/posts/like",
            "/v1/notification"
    };

    String[] PERMIT_USER_POST = {
            "/v1/posts/report/replycomments",
            "/v1/posts/report/comments",
            "/v1/posts/replycomments",
            "/v1/posts/comments",
            "/v1/items",
            "/v1/items/move",
            "/v1/item/move",
            "/v1/folders",
            "/v1/folders/{folderid}/plans",
            "/v1/folders/{folderid}/plan",
            "/v1/posts",
            "/v1/posts/{postid}/good",
            "/v1/posts/image",
            "/v1/auth/reissue"
    };

    String[] PERMIT_USER_PUT = {
            "/v1/users",
            "/v1/posts/replycomments/{replycommentid}",
            "/v1/posts/comments/{commentid}",
            "/v1/items/{itemId}",
            "/v1/folders/plan",
            "/v1/folders/plan/{itemfolderid}",

            
    };

    String[] PERMIT_USER_PATCH = {
            "/v1/folders/{folderid}",
            "/v1/posts/{postid}",
            "/v1/notification/confirm/{notificationId}",
    };

    String[] PERMIT_USER_DELETE = {
            "/v1/posts/replycomments/{replycommentid}",
            "/v1/posts/comments/{commentid}",
            "/v1/users/items/{itemId}",
            "/v1/folders/{folderid}",
            "/v1/folders/{folderid}/plan/{itemfolderid}",
            "/v1/posts/{postid}"
    };


    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.addAllowedOrigin("*");
        configuration.addAllowedHeader("*");
        configuration.addAllowedMethod("*");
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public WebClient webClient() {
        return WebClient.builder().build();
    }
}