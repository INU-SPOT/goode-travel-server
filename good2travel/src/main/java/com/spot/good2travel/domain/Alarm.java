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
public class Alarm {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String message;

    private LocalDateTime localDateTime;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public Alarm(String title, String message, LocalDateTime localDateTime, User user) {
        this.title = title;
        this.message = message;
        this.localDateTime = localDateTime;
        this.user = user;
    }

    public static Alarm of(String title, String body, LocalDateTime localDateTime, User user) {
        return Alarm.builder()
                .title(title)
                .message(body)
                .localDateTime(localDateTime)
                .user(user)
                .build();
    }
}
