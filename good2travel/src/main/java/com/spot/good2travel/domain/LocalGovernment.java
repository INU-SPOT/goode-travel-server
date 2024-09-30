package com.spot.good2travel.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LocalGovernment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String coordinateX;

    private String coordinateY;

    private String todayWeatherUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metropolitan_government_id")
    private MetropolitanGovernment metropolitanGovernment;

    @OneToOne(mappedBy = "localGovernment", cascade = CascadeType.ALL)
    private Weather weather;

    @OneToMany(mappedBy = "localGovernment",cascade = CascadeType.ALL)
    private List<Item> items;

}
