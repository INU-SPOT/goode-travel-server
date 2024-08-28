package com.spot.good2travel.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MetropolitanGovernment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "metropolitanGovernment",cascade = CascadeType.ALL)
    private List<LocalGovernment> localGovernments;

    @OneToMany(mappedBy = "metropolitanGovernment",cascade = CascadeType.ALL)
    private List<User> users;
}
