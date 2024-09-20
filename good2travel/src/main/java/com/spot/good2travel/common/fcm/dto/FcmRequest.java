package com.spot.good2travel.common.fcm.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class FcmRequest {

    @Getter
    public static class FcmMessageDto{

        @Schema(example = "eMogIDKaPnLC6jUJtNeIfn:APA91bGP3G6gUng1cvA-YvMLtPIw90Eqlb30fP9vYYO22wfPQMKKigEXcKGFIVEuPPln23xPSZOqs8WaYWik6PWuOxOLrvUTV5E32jdpLyVS4G5YDpg_2bZf5ITKmpiWmHhfZk6phxxN")
        private String fcmToken;

        @Schema(example = "ㅇㅁㄱ님이 \"인천 여행\" 게시물에 댓글을 달았어요.")
        private String title;

        @Schema(example = "\"아 ㅋㅋ 디자인 이렇게 하는 거 아닌데\"")
        private String body;

        @Schema(example = "true")
        private Boolean validateOnly;
    }



    @Getter
    @Setter
    public static class FcmUpdateDto {
        @Schema(example = "eMogIDKaPnLC6jUJtNeIfn:APA91bGP3G6gUng1cvA-YvMLtPIw90Eqlb30fP9vYYO22wfPQMKKigEXcKGFIVEuPPln23xPSZOqs8WaYWik6PWuOxOLrvUTV5E32jdpLyVS4G5YDpg_2bZf5ITKmpiWmHhfZk6phxxN")
        private String fcmToken;
    }
}
