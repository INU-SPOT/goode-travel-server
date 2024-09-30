package com.spot.good2travel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class WeatherResponse {

    @Schema(example = "경상북도")
    private final String metropolitanGovernmentName;

    @Schema(example = "구미시")
    private final String localGovernmentName;

    @Schema(example = "2021-09-29")
    private final LocalDate date;

    @Schema(example = "맑음")
    private final String sky;

    @Schema(example = "23.5")
    private final String temperature;

    @Schema(example = "day")
    private final String day;

    @Schema(example = "https://krweb.wni.com/mv4/html/weekly.html?loc=4719000000")
    private final String todayWeatherLink;

    @Builder
    public WeatherResponse(String metropolitanGovernmentName, String localGovernmentName, LocalDate date, String sky, String temperature, String day, String todayWeatherLink){
        this.metropolitanGovernmentName = metropolitanGovernmentName;
        this.localGovernmentName = localGovernmentName;
        this.date = date;
        this.sky = sky;
        this.temperature = temperature;
        this.day = day;
        this.todayWeatherLink = todayWeatherLink;
    }

    public static WeatherResponse of(String metropolitanGovernmentName, String localGovernmentName,
                                     LocalDate date, String sky, String temperature, String day, String todayWeatherLink){
        return WeatherResponse.builder()
                .metropolitanGovernmentName(metropolitanGovernmentName)
                .localGovernmentName(localGovernmentName)
                .date(date)
                .sky(sky)
                .temperature(temperature)
                .day(day)
                .todayWeatherLink(todayWeatherLink)
                .build();
    }

}
