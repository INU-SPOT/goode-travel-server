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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.spot.good2travel.dto.UserRequest.UserRegisterUpdateRequest;
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

        return user.getNickname() != null;
    }

    public UserInfoResponse userRegisterUpdate(UserRegisterUpdateRequest request, UserDetails userDetails){
        Long userId = ((CustomUserDetails) userDetails).getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_NOT_FOUND));
        MetropolitanGovernment metropolitanGovernment = metropolitanGovernmentRepository.findById(request.getMetropolitanGovernmentId())
                .orElseThrow(()-> new NotFoundElementException(ExceptionMessage.METROPOLITANGOVERNMENT_NOT_FOUND));

        user.updateUser(request.getNickname(), request.getProfileImageName(), metropolitanGovernment);
        userRepository.save(user);

        return UserInfoResponse.of(user.getNickname(), user.getMetropolitanGovernment().getName(), user.getProfileImageName());
    }

    @Transactional
    public UserInfoResponse getUserInfo(UserDetails userDetails){
        Long userId = ((CustomUserDetails) userDetails).getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.MEMBER_NOT_FOUND));
        String imageName = user.getProfileImageName();

        UserInfoResponse response = UserResponse.UserInfoResponse
                .of(user.getNickname(), user.getMetropolitanGovernment().getName(), imageName);

        return response;
    }
}
