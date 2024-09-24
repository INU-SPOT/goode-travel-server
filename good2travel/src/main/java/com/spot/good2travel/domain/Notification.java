package com.spot.good2travel.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long postId;

    private String title;

    private String message;

    private LocalDateTime notificationTime;

    private Boolean isConfirm;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Notification(Long postId, String title, String message, LocalDateTime notificationTime, User user, Boolean isConfirm) {
        this.postId = postId;
        this.title = title;
        this.message = message;
        this.notificationTime = notificationTime;
        this.user = user;
        this.isConfirm = isConfirm;
    }

    public static Notification of(Long postId, String title, String body, LocalDateTime notificationTime, User user) {
        return Notification.builder()
                .postId(postId)
                .title(title)
                .message(body)
                .notificationTime(notificationTime)
                .user(user)
                .isConfirm(false)
                .build();
    }

    public void updateConfirm() {
        this.isConfirm = !isConfirm;
    }
}
