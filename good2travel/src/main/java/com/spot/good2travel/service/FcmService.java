package com.spot.good2travel.service;

import com.google.firebase.messaging.*;
import com.spot.good2travel.common.exception.ExceptionMessage;
import com.spot.good2travel.common.exception.NotFoundElementException;
import com.spot.good2travel.common.fcm.FcmRequest;
import com.spot.good2travel.common.security.CustomUserDetails;
import com.spot.good2travel.domain.Fcm;
import com.spot.good2travel.domain.User;
import com.spot.good2travel.repository.FcmRepository;
import com.spot.good2travel.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@RequiredArgsConstructor
@Slf4j
@Service
public class FcmService {

    private final FcmRepository fcmRepository;
    private final UserRepository userRepository;

    public String sendMessage(String token, String title, String body) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setToken(token)
                .setWebpushConfig(WebpushConfig.builder().putHeader("ttl", "300")
                        .setNotification(new WebpushNotification(title, body))
                        //.setFcmOptions()) url 정해지면 생각
                        .build())
                .build();

        return FirebaseMessaging.getInstance().send(message);
    }

    @Transactional
    public void updateToken(FcmRequest.FcmUpdateDto fcmUpdateDto, UserDetails userDetails){
        Long id = ((CustomUserDetails) userDetails).getId();
        Optional<Fcm> fcm = fcmRepository.findByUserId(1L);
        if (fcm.isPresent()) {
            fcm.get().toUpdate(fcmUpdateDto.getFcmToken());
        } else {
            User user = userRepository.findById(id)
                    .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.USER_NOT_FOUND));
            fcmRepository.save(Fcm.builder()
                    .fcmToken(fcmUpdateDto.getFcmToken())
                    .user(user)
                    .build());
        }
    }
}
