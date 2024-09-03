package com.spot.good2travel.common.fcm.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FcmRequestDto {
    private String fcmToken;
    private String title;
    private String body;
    private boolean validateOnly;


    @Getter
    @Setter
    public static class FcmUpdateDto {
        private String fcmToken;
    }
}
