package com.spot.good2travel.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String firstContent;

    private String lastContent;

    private Integer good;

    private Integer report;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Integer> sequence;

    private LocalDate startDate;

    private LocalDate endDate;

}
