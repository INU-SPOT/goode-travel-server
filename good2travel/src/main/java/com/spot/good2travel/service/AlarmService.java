package com.spot.good2travel.service;

import com.spot.good2travel.common.security.CustomUserDetails;
import com.spot.good2travel.domain.Alarm;
import com.spot.good2travel.dto.AlarmResponse;
import com.spot.good2travel.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final AlarmRepository alarmRepository;

    public List<AlarmResponse> getAlarms(UserDetails userDetails) {
        Long userId = ((CustomUserDetails) userDetails).getId();

        Optional<List<Alarm>> alarms = alarmRepository.findByUserId(userId);
        return alarms.map(alarmList -> alarmList.stream()
                .map(AlarmResponse::of)
                        .sorted(Comparator.comparing(AlarmResponse::getLocalDateTime).reversed())
                .toList())
                .orElse(null);
    }
}
