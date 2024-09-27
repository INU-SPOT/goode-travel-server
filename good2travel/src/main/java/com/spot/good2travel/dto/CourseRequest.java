package com.spot.good2travel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;

@Getter
public class CourseRequest {

    @Getter
    public static class CourseCreateUpdateRequest{
        @Schema(example = "1")
        private List<Long> items;

    }

}
