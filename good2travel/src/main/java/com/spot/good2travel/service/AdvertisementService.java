package com.spot.good2travel.service;

import com.spot.good2travel.common.exception.ExceptionMessage;
import com.spot.good2travel.common.exception.NotFoundElementException;
import com.spot.good2travel.domain.Advertisement;
import com.spot.good2travel.domain.LocalGovernment;
import com.spot.good2travel.dto.AdvertisementRequest;
import com.spot.good2travel.dto.AdvertisementResponse;
import com.spot.good2travel.repository.AdvertisementRepository;
import com.spot.good2travel.repository.LocalGovernmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AdvertisementService {

    private final LocalGovernmentRepository localGovernmentRepository;
    private final AdvertisementRepository advertisementRepository;


    public Long createAdItem(AdvertisementRequest.AdItemCreateRequest adItemCreateRequest) {
        LocalGovernment localGovernment = localGovernmentRepository.findById(adItemCreateRequest.getLocalGovernmentId())
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.LOCALGOVERNMENT_NOT_FOUND));
//        Course course = courseRepository.findById(adItemCreateRequest.getCourseId()); //관광코스와 연결
        Advertisement adItem = Advertisement.ofAd(adItemCreateRequest, localGovernment);
        return advertisementRepository.save(adItem).getId();
    }

    public AdvertisementResponse.AdDetailResponse getAdItem(Long advertisementId){
        Advertisement ad = advertisementRepository.findById(advertisementId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.ITEM_NOT_FOUND));
        return AdvertisementResponse.AdDetailResponse.of(ad);
    }
}
