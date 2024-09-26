package com.spot.good2travel.service;

import com.spot.good2travel.common.exception.ExceptionMessage;
import com.spot.good2travel.common.exception.NotFoundElementException;
import com.spot.good2travel.common.security.CustomUserDetails;
import com.spot.good2travel.domain.Notification;
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
}
