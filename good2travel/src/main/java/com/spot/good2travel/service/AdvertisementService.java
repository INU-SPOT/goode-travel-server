package com.spot.good2travel.service;

import com.spot.good2travel.common.exception.ExceptionMessage;
import com.spot.good2travel.common.exception.NotFoundElementException;
import com.spot.good2travel.domain.Advertisement;
import com.spot.good2travel.domain.LocalGovernment;
import com.spot.good2travel.dto.AdvertisementRequest;
import com.spot.good2travel.repository.AdvertisementRepository;
import com.spot.good2travel.repository.LocalGovernmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AdvertisementService {

    private final LocalGovernmentRepository localGovernmentRepository;
    private final AdvertisementRepository advertisementRepository;


    public Long createAdItem(AdvertisementRequest.AdItemCreateUpdateRequest adItemCreateUpdateRequest) {
        LocalGovernment localGovernment = localGovernmentRepository.findById(adItemCreateUpdateRequest.getLocalGovernmentId())
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.LOCAL_GOVERNMENT_NOT_FOUND));
        Advertisement adItem = Advertisement.ofAd(adItemCreateUpdateRequest, localGovernment);
        return advertisementRepository.save(adItem).getId();
    }

    @Transactional
    public Long updateAdItem(Long adId, AdvertisementRequest.AdItemCreateUpdateRequest adItemCreateUpdateRequest) {
        Advertisement advertisement = advertisementRepository.findById(adId)
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.ADVERTISEMENT_NOT_FOUND));
        LocalGovernment localGovernment = localGovernmentRepository.findById(adItemCreateUpdateRequest.getLocalGovernmentId())
                .orElseThrow(() -> new NotFoundElementException(ExceptionMessage.LOCAL_GOVERNMENT_NOT_FOUND));
        advertisement.updateAd(adItemCreateUpdateRequest, localGovernment);
        return advertisement.getId();
    }

    @Transactional
    public void deleteAdItem(Long adId) {
        advertisementRepository.deleteById(adId);
    }
}
