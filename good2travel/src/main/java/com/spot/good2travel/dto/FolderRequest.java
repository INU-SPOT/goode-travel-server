package com.spot.good2travel.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FolderRequest {

    @Getter
    @Setter
    @Builder
    public static class FolderCreate{
        private String title;

    }
}
