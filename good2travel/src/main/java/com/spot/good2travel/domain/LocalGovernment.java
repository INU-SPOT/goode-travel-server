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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "metropolitan_government_id")
    private MetropolitanGovernment metropolitanGovernment;

    @OneToMany(mappedBy = "localGovernment", cascade = CascadeType.ALL)
    private List<Weather> weathers;

    @OneToMany(mappedBy = "localGovernment",cascade = CascadeType.ALL)
    private List<Item> items;

}
