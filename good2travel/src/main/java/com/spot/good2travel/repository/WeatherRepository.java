package com.spot.good2travel.repository;

import com.spot.good2travel.domain.LocalGovernment;
import com.spot.good2travel.domain.Weather;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WeatherRepository extends JpaRepository<Weather, Long> {

    Weather findByLocalGovernment(LocalGovernment localGovernment);
}
