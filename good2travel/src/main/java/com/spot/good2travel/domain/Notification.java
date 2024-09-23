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

    private String title;

    private String message;

    private LocalDateTime localDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Notification(String title, String message, LocalDateTime localDateTime, User user) {
        this.title = title;
        this.message = message;
        this.localDateTime = localDateTime;
        this.user = user;
    }

    public static Notification of(String title, String body, LocalDateTime localDateTime, User user) {
        return Notification.builder()
                .title(title)
                .message(body)
                .localDateTime(localDateTime)
                .user(user)
                .build();
    }
}
