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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Notification(Long postId, String title, String message, LocalDateTime notificationTime, User user) {
        this.postId = postId;
        this.title = title;
        this.message = message;
        this.notificationTime = notificationTime;
        this.user = user;
    }

    public static Notification of(Long postId, String title, String body, LocalDateTime notificationTime, User user) {
        return Notification.builder()
                .postId(postId)
                .title(title)
                .message(body)
                .notificationTime(notificationTime)
                .user(user)
                .build();
    }
}
