package com.spot.good2travel.dto;

import com.spot.good2travel.dto.record.Goode;
import com.spot.good2travel.dto.record.Plan;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class ItemListResponse {
    List<Goode> goodes;
    List<Plan> plans;
}

