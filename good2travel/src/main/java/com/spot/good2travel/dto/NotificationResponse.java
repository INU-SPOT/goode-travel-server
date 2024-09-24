package com.spot.good2travel.dto;

import com.spot.good2travel.domain.Notification;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class NotificationResponse {

    @Schema(example = "1")
    private final Long id;

    @Schema(example = "1")
    private final Long postId;

    @Schema(example = "깨구링님이 '환기리의 꿀잼 인천여행' 게시물에 댓글을 달았어요.")
    private final String title;

    @Schema(example = "아 ㅋㅋ 내용 개구린데요?")
    private final String message;

    @Schema(example = "2024-09-21T22:25:39.03422")
    private final LocalDateTime notificationTime;

    @Schema(example = "false")
    private final Boolean isConfirm;

    @Builder
    public NotificationResponse(Long id, Long postId, String title, String message, LocalDateTime notificationTime, Boolean isConfirm) {
        this.id = id;
        this.postId = postId;
        this.title = title;
        this.message = message;
        this.notificationTime = notificationTime;
        this.isConfirm = isConfirm;
    }

    public static NotificationResponse of(Notification notification){
        return NotificationResponse.builder()
                .id(notification.getId())
                .postId(notification.getPostId())
                .title(notification.getTitle())
                .message(notification.getMessage())
                .notificationTime(notification.getNotificationTime())
                .isConfirm(notification.getIsConfirm())
                .build();
    }
}
