package com.spot.good2travel.dto;

import com.spot.good2travel.domain.Alarm;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AlarmResponse {

    private String title;
    private String message;
    private LocalDateTime localDateTime;

    @Builder
    public AlarmResponse(String title, String message, LocalDateTime localDateTime) {
        this.title = title;
        this.message = message;
        this.localDateTime = localDateTime;
    }

    public static AlarmResponse of(Alarm alarm){
        return AlarmResponse.builder()
                .title(alarm.getTitle())
                .message(alarm.getMessage())
                .localDateTime(alarm.getLocalDateTime())
                .build();
    }
}
