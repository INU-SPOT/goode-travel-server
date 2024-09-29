package com.spot.good2travel.domain;

import com.spot.good2travel.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Weather extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;

    private String temperature;

    private String sky;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_government_id")
    private LocalGovernment localGovernment;


    @Builder
    public Weather(LocalDate date, String temperature, String sky,LocalGovernment localGovernment){
        this.date = date;
        this.temperature = temperature;
        this.sky = sky;
        this.localGovernment = localGovernment;
    }

    public static Weather of(LocalGovernment localGovernment){
        return Weather.builder().localGovernment(localGovernment).build();
    }

    public Weather updateWeather(String temperature, String sky){
        this.temperature = temperature;
        this.sky = sky;
        return this;
    }

    public Weather updateDate(LocalDate date){
        this.date = date;

        return this;
    }

}
