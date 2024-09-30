package com.spot.good2travel.dto;

import com.spot.good2travel.domain.Item;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


public class ItemResponse {

    @Getter
    @Builder
    public static class GoodeDetailsResponse {

        @Schema(example = "굳이? 성심당 가서 망고시루 먹기")
        private String title;

        @Schema(example = "/image/~")
        private String imageUrl;

        @Schema(example = "1956년 대전역 앞 작은 찐빵집에서 시작한 ..~")
        private String description;

        @Schema(example = "대전광역시 무슨구 무슨로 1234")
        private String address;

        @Schema(example = "대전광역시")
        private String localGovernmentName;

        public static GoodeDetailsResponse of(Item item, String localGovernmentName){
            return GoodeDetailsResponse.builder()
                    .title(item.getTitle())
                    .imageUrl(item.getImageUrl())
                    .address(item.getAddress())
                    .description(item.getDescription())
                    .localGovernmentName(localGovernmentName)
                    .build();
        }
    }

    @Getter
    @Builder
    public static class GoodeRandomResponse {

        @Schema(example = "1")
        private Long itemId;

        @Schema(example = "굳이? 성심당 가서 망고시루 먹기")
        private String title;

        public static GoodeRandomResponse of (Item item){
            return GoodeRandomResponse.builder()
                    .itemId(item.getId())
                    .title(item.getTitle())
                    .build();
        }
    }

    @Getter
    public static class GoodeThumbnailResponse{
        private final Long itemId;
        private final String metropolitanGovernmentName;
        private final String localGovernmentName;
        private final String title;
        private final String imageUrl;

        @Builder
        public GoodeThumbnailResponse(Long itemId, String metropolitanGovernmentName, String localGovernmentName, String title, String imageUrl){
            this.itemId = itemId;
            this.metropolitanGovernmentName = metropolitanGovernmentName;
            this.localGovernmentName = localGovernmentName;
            this.title = title;
            this.imageUrl = imageUrl;
        }

        public static GoodeThumbnailResponse of(Item item, String imageUrl){
            return GoodeThumbnailResponse.builder()
                    .itemId(item.getId())
                    .metropolitanGovernmentName(item.getLocalGovernment() != null ? item.getLocalGovernment().getMetropolitanGovernment().getName() : null)
                    .localGovernmentName(item.getLocalGovernment() != null ? item.getLocalGovernment().getName() : null)
                    .title(item.getTitle())
                    .imageUrl(imageUrl)
                    .build();
        }
    }

    @Getter
    public static class RecommendGoodeResponse{
        private final Long itemId;
        private final String localGovernmentName;
        private final String title;
        private final String imageUrl;

        private final List<CourseResponse.CourseThumbnailResponse> courses;

        @Builder
        public RecommendGoodeResponse(Long itemId, String localGovernmentName, String title, String imageUrl, List<CourseResponse.CourseThumbnailResponse> courses) {
            this.itemId = itemId;
            this.localGovernmentName = localGovernmentName;
            this.title = title;
            this.imageUrl = imageUrl;
            this.courses = courses;
        }

        public static RecommendGoodeResponse of(Item item, List<CourseResponse.CourseThumbnailResponse> courses){
            return RecommendGoodeResponse.builder()
                    .itemId(item.getId())
                    .localGovernmentName(item.getLocalGovernment().getName())
                    .title(item.getTitle())
                    .imageUrl(item.getImageUrl())
                    .courses(courses)
                    .build();
        }
    }
}
