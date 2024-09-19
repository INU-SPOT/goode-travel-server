package com.spot.good2travel.dto;

import com.spot.good2travel.domain.ItemType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ItemRequest {

    @Getter
    @Setter
    public static class OfficialItemCreateRequest {
        @Schema(example = "GOODE")
        @NotBlank
        private ItemType type;

        @NotBlank
        @Schema(example = "굳이? 성심당 가서 망고시루 먹기")
        private String title;

        @Schema(example = "/image/~")
        private String imageUrl;

        @Schema(example = "1956년 대전역 앞 작은 찐빵집에서 시작한 ..~")
        private String description;

        @NotBlank
        @Schema(example = "대전광역시 무슨구 무슨로 1234")
        private String address;

        @Schema(example = "[1,2]")
        private List<Long> categories;

        @NotBlank
        @Schema(example = "1")
        private Long localGovernmentId;
    }

    @Getter
    @Setter
    public static class ItemCreateRequest {
        @Schema(example = "PLAN")
        @NotBlank
        private ItemType type;

        @NotBlank
        @Schema(example = "남선 공원에서 산책하기")
        private String title;

        @NotBlank
        @Schema(example = "/image/~")
        private String imageUrl;

        @Schema(example = "🐋")
        private String emoji;

        @NotBlank
        @Schema(example = "1")
        private Long localGovernmentId;
    }

    @Getter
    @Setter
    public static class ItemUpdateRequest{
        @Schema(example = "PLAN")
        private ItemType type;

        @Schema(example = "남선 공원에서 산책하기")
        private String title;

        @Schema(example = "/image/~")
        private String imageUrl;

        @Schema(example = "🐋")
        private String emoji;

        @NotBlank
        @Schema(example = "1")
        private Long localGovernmentId;
    }
}
