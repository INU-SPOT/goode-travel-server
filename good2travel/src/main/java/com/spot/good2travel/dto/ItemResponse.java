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
}
