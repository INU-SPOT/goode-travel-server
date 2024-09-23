package com.spot.good2travel.service;

import com.spot.good2travel.common.security.CustomUserDetails;
import com.spot.good2travel.domain.Notification;
import com.spot.good2travel.dto.NotificationResponse;
import com.spot.good2travel.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public List<NotificationResponse> getNotifications(UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();

        Optional<List<Notification>> alarms = notificationRepository.findByUserId(userId);
        return alarms.map(alarmList -> alarmList.stream()
                .map(NotificationResponse::of)
                        .sorted(Comparator.comparing(NotificationResponse::getLocalDateTime).reversed())
                .toList())
                .orElse(null);
    }
}
