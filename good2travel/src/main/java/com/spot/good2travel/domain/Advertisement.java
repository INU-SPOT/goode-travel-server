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

    private String imageNames;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "localgovernment_id")
    private LocalGovernment localGovernment;

//    private Course coursdId;

    @Builder
    public Advertisement(String title, String link, String address, String imageNames, LocalGovernment localGovernment) {
        this.title = title;
        this.link = link;
        this.address = address;
        this.imageNames = imageNames;
        this.localGovernment = localGovernment;
    }

    public static Advertisement ofAd(AdvertisementRequest.AdItemCreateRequest adItemCreateRequest, LocalGovernment localGovernment) {
        return Advertisement.builder()
                .title(adItemCreateRequest.getTitle())
                .link(adItemCreateRequest.getAdLink())
                .address(adItemCreateRequest.getAddress())
                .imageNames(adItemCreateRequest.getImageNames())
                .localGovernment(localGovernment)
                .build();
    }

}
