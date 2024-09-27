package com.spot.good2travel.dto;

import lombok.Getter;

import java.util.List;

@Getter
public class CourseRequest {

    @Getter
    public static class CourseCreateUpdateRequest{

        private List<Long> items;

    }

}
