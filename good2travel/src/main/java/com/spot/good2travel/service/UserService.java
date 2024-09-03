package com.spot.good2travel.service;

import com.spot.good2travel.common.exception.ExceptionMessage;
import com.spot.good2travel.common.exception.NotFoundElementException;
import com.spot.good2travel.common.security.CustomUserDetails;
import com.spot.good2travel.domain.User;
import com.spot.good2travel.dto.UserInfoResponse;
import com.spot.good2travel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public Boolean isRegistered(UserDetails userDetails){
        Long userId = ((CustomUserDetails) userDetails).getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_NOT_FOUND));

        if(user.getNickname() == null){
            return false;
        }
        return true;
    }

    public void register(String nickname, MultipartFile image, UserDetails userDetails){
        Long userId = ((CustomUserDetails) userDetails).getId();

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_NOT_FOUND));

        user.updateUser(nickname, null);
        userRepository.save(user);
    }

    public UserInfoResponse getUserInfo(UserDetails userDetails){
        Long userId = ((CustomUserDetails) userDetails).getId();

        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_NOT_FOUND));

        UserInfoResponse response = UserInfoResponse.of(user.getNickname(), user.getProfileImageUrl());

        return response;
    }
}
