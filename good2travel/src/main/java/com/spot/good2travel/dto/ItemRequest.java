package com.spot.good2travel.dto;

import com.spot.good2travel.domain.ItemType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.util.List;

public class ItemRequest {

    @Getter
    public static class OfficialItemCreateRequest {
        @NotBlank
        @Schema(example = "GOODE")
        private ItemType type;

        @NotBlank
        @Schema(example = "굳이? 성심당 가서 망고시루 먹기")
        private String title;

        @Schema(example = "https://~")
        private String imageUrl;

        @Schema(example = "1956년 대전역 앞 작은 찐빵집에서 시작한 ..~")
        private String description;

        @NotBlank
        @Schema(example = "대전광역시 무슨구 무슨로 1234")
        private String address;

        @Schema(example = "[1,2]")
        private List<Long> categories;

        @NotBlank
        @Schema(example = "67")
        private Long localGovernmentId;
    }

    @Getter
    public static class ItemCreateRequest {
        @NotBlank
        @Schema(example = "PLAN")
        private ItemType type;

        @NotBlank
        @Schema(example = "남선 공원에서 산책하기")
        private String title;

        @NotBlank
        @Schema(example = "https://~")
        private String imageUrl;

        @NotBlank
        @Schema(example = "68")
        private Long localGovernmentId;
    }

    @Getter
    public static class ItemUpdateRequest{
        @Schema(example = "PLAN")
        private ItemType type;

        @Schema(example = "남선 공원에서 산책하기")
        private String title;

        @Schema(example = "https://~")
        private String imageUrl;

        @NotBlank
        @Schema(example = "68")
        private Long localGovernmentId;
    }

}
