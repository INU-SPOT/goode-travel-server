package com.spot.good2travel.dto;

import com.spot.good2travel.domain.Alarm;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AlarmResponse {

    @Schema(example = "1")
    private final Long id;

    @Schema(example = "깨구링님이 '환기리의 꿀잼 인천여행' 게시물에 댓글을 달았어요.")
    private final String title;

    @Schema(example = "아 ㅋㅋ 내용 개구린데요?")
    private final String message;

    @Schema(example = "2024-09-21T22:25:39.03422")
    private final LocalDateTime localDateTime;

    @Builder
    public AlarmResponse(Long id, String title, String message, LocalDateTime localDateTime) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.localDateTime = localDateTime;
    }

    public static AlarmResponse of(Alarm alarm){
        return AlarmResponse.builder()
                .id(alarm.getId())
                .title(alarm.getTitle())
                .message(alarm.getMessage())
                .localDateTime(alarm.getLocalDateTime())
                .build();
    }
}
