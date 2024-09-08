package com.spot.good2travel.service;

import com.spot.good2travel.common.exception.ExceptionMessage;
import com.spot.good2travel.common.exception.NotFoundElementException;
import com.spot.good2travel.common.security.CustomUserDetails;
import com.spot.good2travel.domain.MetropolitanGovernment;
import com.spot.good2travel.domain.User;
import com.spot.good2travel.dto.UserResponse;
import com.spot.good2travel.repository.MetropolitanGovernmentRepository;
import com.spot.good2travel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static com.spot.good2travel.dto.UserResponse.UserInfoResponse;
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;
    private final MetropolitanGovernmentRepository metropolitanGovernmentRepository;

    public Boolean isRegistered(UserDetails userDetails){
        Long userId = ((CustomUserDetails) userDetails).getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_NOT_FOUND));

        if(user.getNickname() == null){
            return false;
        }
        return true;
    }

    public void register(String nickname, MultipartFile image, Long metroId,UserDetails userDetails){
        Long userId = ((CustomUserDetails) userDetails).getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_NOT_FOUND));
        MetropolitanGovernment metropolitanGovernment = metropolitanGovernmentRepository.findById(metroId).orElseThrow(()-> new NotFoundElementException(ExceptionMessage.MEMBER_NOT_FOUND));
        user.updateUser(nickname, null, metropolitanGovernment);
        userRepository.save(user);
    }

    public UserInfoResponse getUserInfo(UserDetails userDetails){
        Long userId = ((CustomUserDetails) userDetails).getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_NOT_FOUND));

        UserInfoResponse response = UserResponse.UserInfoResponse.of(user.getNickname(), user.getProfileImageUrl());

        return response;
    }
}
