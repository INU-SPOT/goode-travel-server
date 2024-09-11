package com.spot.good2travel.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


public class FolderRequest {

    @Getter
    public static class FolderCreateRequest{
        private String title;
    }
    @Getter
    public static class PlanListUpdateRequest {
        private List<Integer> sequence;
    }

    @Getter
    public static class FolderTitleUpdateRequest{
        private String title;
    }
}
