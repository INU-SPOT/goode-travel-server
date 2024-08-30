package com.spot.good2travel.service;

import com.spot.good2travel.domain.User;
import com.spot.good2travel.repository.UserRepository;
import com.spot.good2travel.common.security.OAuth2Attributes;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomOAuth2Service implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> service = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = service.loadUser(userRequest);

        Map<String, Object> originAttributes = oAuth2User.getAttributes();

        String provider = userRequest.getClientRegistration().getRegistrationId();

        OAuth2Attributes attributes = OAuth2Attributes.of(provider, originAttributes);
        save(attributes);
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        return new DefaultOAuth2User(authorities, attributes.getAttributes(), attributes.getNameAttributesKey());
    }

    private User save(OAuth2Attributes authAttributes) {
        Optional<User> user = userRepository.findByEmailAndProvider(authAttributes.getEmail(), authAttributes.getProvider());
        User returnMember;

        if (user.isEmpty()) {
            returnMember = new User(authAttributes.getProvider(), authAttributes.getEmail(),
                    null, null, List.of("ROLE_USER"));
        } else {
            returnMember = user.get();
        }

        return userRepository.save(returnMember);
    }
}