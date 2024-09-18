package com.spot.good2travel.common.security;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Getter
@ToString
@Slf4j
public class OAuth2Attributes {
    private final Map<String, Object> attributes;     // OAuth2 반환하는 유저 정보
    private final String nameAttributesKey;
    private final String name;
    private final String email;
    private final String profileImageUrl;
    private final String provider;

    @Builder
    public OAuth2Attributes(Map<String, Object> attributes, String nameAttributesKey,
                            String name, String email, String profileImageUrl, String provider) {
        this.attributes = attributes;
        this.nameAttributesKey = nameAttributesKey;
        this.name = name;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.provider = provider;
    }

    public static OAuth2Attributes of(String socialName, Map<String, Object> attributes) {
        if ("kakao".equals(socialName)) {
            return ofKakao("email", attributes);
        } else if ("google".equals(socialName)) {
            return ofGoogle("email", attributes);
        }
        return ofNaver("id", attributes);
    }


    private static OAuth2Attributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes) {
        return OAuth2Attributes.builder()
                .name(String.valueOf(attributes.get("name")))
                .email(String.valueOf(attributes.get("email")))
                .profileImageUrl(String.valueOf(attributes.get("picture")))
                .attributes(attributes)
                .nameAttributesKey(userNameAttributeName)
                .provider("google")
                .build();
    }

    private static OAuth2Attributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");
        log.info(kakaoAccount.toString());

        return OAuth2Attributes.builder()
                .name(String.valueOf(kakaoProfile.get("nickname")))
                .email(String.valueOf(kakaoAccount.get("email")))
                .profileImageUrl(String.valueOf(kakaoProfile.get("profile_image_url")))
                .attributes(kakaoAccount)
                .nameAttributesKey(userNameAttributeName)
                .provider("kakao")
                .build();
    }

    private static OAuth2Attributes ofNaver(String userNameAttributeName, Map<String, Object> attributes) {
        Map<String, Object> response = (Map<String, Object>) attributes.get("response");

        return OAuth2Attributes.builder()
                .name(String.valueOf(response.get("name")))
                .email(String.valueOf(response.get("id")))
                .profileImageUrl(String.valueOf(response.get("profile_image")))
                .attributes(response)
                .nameAttributesKey(userNameAttributeName)
                .provider("naver")
                .build();
    }

}