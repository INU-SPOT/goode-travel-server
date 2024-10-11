package com.spot.good2travel.service;

import com.spot.good2travel.common.exception.ExceptionMessage;
import com.spot.good2travel.common.exception.JwtEmptyException;
import com.spot.good2travel.common.exception.NotAuthorizedUserException;
import com.spot.good2travel.common.exception.NotFoundElementException;
import com.spot.good2travel.common.security.CustomUserDetails;
import com.spot.good2travel.domain.Notification;
import com.spot.good2travel.domain.User;
import com.spot.good2travel.dto.NotificationResponse;
import com.spot.good2travel.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<NotificationResponse> getNotifications(UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();

        List<Notification> alarms = notificationRepository.findByUserIdOrderByNotificationTimeDesc(userId);
        return alarms.stream()
                .map(NotificationResponse::of)
                .toList();
    }

    @Transactional
    public Boolean updateConfirm(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.NOTIFICATION_NOT_FOUND));

        notification.updateConfirm();
        return notification.getIsConfirm();
    }

    @Transactional
    public void deleteConfirm(Long notificationId, UserDetails userDetails) {
        Notification notification = notificationRepository.findById(notificationId)
                        .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.NOTIFICATION_NOT_FOUND));
        validIsOwner(notification.getUser(), userDetails);
        notificationRepository.deleteById(notificationId);
    }

    public void validIsOwner(User user, UserDetails userDetails){
        if(userDetails == null){
                throw new JwtEmptyException(ExceptionMessage.TOKEN_NOT_FOUND);
            }
            Long userId = ((CustomUserDetails) userDetails).getId();
            if(!user.getId().equals(userId)){
                throw new NotAuthorizedUserException(ExceptionMessage.USER_UNAUTHENTICATED);
        }
    }
}
