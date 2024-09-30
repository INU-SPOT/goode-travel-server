package com.spot.good2travel.domain;

import com.spot.good2travel.dto.AdvertisementRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Advertisement {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String link;

    private String address;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "localgovernment_id")
    private LocalGovernment localGovernment;

    @Builder
    public Advertisement(String title, String link, String address, LocalGovernment localGovernment) {
        this.title = title;
        this.link = link;
        this.address = address;
        this.localGovernment = localGovernment;
    }

    public static Advertisement ofAd(AdvertisementRequest.AdCreateUpdateRequest adCreateUpdateRequest, LocalGovernment localGovernment) {
        return Advertisement.builder()
                .title(adCreateUpdateRequest.getTitle())
                .link(adCreateUpdateRequest.getAdLink())
                .address(adCreateUpdateRequest.getAddress())
                .localGovernment(localGovernment)
                .build();
    }

    public void updateAd(AdvertisementRequest.AdCreateUpdateRequest adCreateUpdateRequest, LocalGovernment localGovernment) {
        this.title = adCreateUpdateRequest.getTitle();
        this.link = adCreateUpdateRequest.getAdLink();
        this.address = adCreateUpdateRequest.getAddress();
        this.localGovernment = localGovernment;
    }
}
