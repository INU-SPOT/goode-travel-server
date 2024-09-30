package com.spot.good2travel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

public class AdvertisementRequest {

    @Getter
    public static class AdCreateUpdateRequest {

        @NotBlank
        @Schema(example = "(AD) 호텔 오노마 대전 숙박하기")
        private String title;

        @NotBlank
        @Schema(example = "대전광역시 유성구 엑스포로 1")
        private String address;

        @NotBlank
        @Schema(example = "https://www.hotels.com/affiliates/hotel-onoma-daejeon-otogeulaepeu-keollegsyeon-daejeon-hangug.aiLj82i")
        private String adLink;

        @NotBlank
        @Schema(example = "69")
        private Long localGovernmentId;
    }
}
