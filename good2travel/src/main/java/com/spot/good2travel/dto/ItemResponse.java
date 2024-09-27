package com.spot.good2travel.dto;

import com.spot.good2travel.domain.Item;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

public class ItemResponse {

    @Getter
    @Setter
    @Builder
    public static class GoodeDetailsResponse {
        private String title;
        private String imageUrl;
        private String address;
        private String description;
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

        public static GoodeThumbnailResponse of(Item item){
            return GoodeThumbnailResponse.builder()
                    .itemId(item.getId())
                    .metropolitanGovernmentName(item.getLocalGovernment() != null ? item.getLocalGovernment().getMetropolitanGovernment().getName() : null)
                    .localGovernmentName(item.getLocalGovernment() != null ? item.getLocalGovernment().getName() : null)
                    .title(item.getTitle())
                    .imageUrl(item.getImageUrl())
                    .build();
        }
    }
}
