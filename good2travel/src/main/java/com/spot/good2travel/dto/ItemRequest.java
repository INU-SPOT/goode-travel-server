package com.spot.good2travel.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

public class ItemRequest {

    @Getter
    @Setter
    public static class ItemCreateRequest {
        private String type;
        private String title;
        private String imageUrl;
        private String description;
        private String address;
        private Boolean isOfficial;
        private String emoji;
        private List<Long> categories;
        private Long localGovernmentId;
    }
}
