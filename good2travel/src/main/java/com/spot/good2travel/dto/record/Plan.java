package com.spot.good2travel.dto.record;

import java.time.LocalDate;

public record Plan(Long id, String title, String image, LocalDate createDate, Boolean isFinished) {
}
