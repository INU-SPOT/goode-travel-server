package com.spot.good2travel.dto;

import lombok.Getter;

import java.util.List;


public class FolderRequest {

    @Getter
    public static class FolderCreateRequest{
        private String title;
    }
    @Getter
    public static class FolderUpdateRequest {
        private String title;
        private List<Integer> sequence;
    }
}
