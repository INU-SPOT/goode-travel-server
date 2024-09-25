package com.spot.good2travel.dto;

import com.spot.good2travel.domain.Advertisement;
import com.spot.good2travel.domain.ItemType;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class AdvertisementResponse {

    @Getter
    @Builder
    public static class AdDetailResponse{
        private ItemType type;
        private String title;
        private String address;
        private String link;
        private List<String> imageName;

        public static AdDetailResponse of(Advertisement ad){
            return AdDetailResponse.builder()
                    .title(ad.getTitle())
                    .address(ad.getAddress())
                    .link(ad.getLink())
                    .imageName(List.of(ad.getImageNames().split(",")))
                    .build();
        }

    }
}
