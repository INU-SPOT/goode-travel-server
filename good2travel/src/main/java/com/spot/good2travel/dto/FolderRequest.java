package com.spot.good2travel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


public class FolderRequest {

    @Getter
    public static class FolderCreateRequest{
        @Schema(example = "나의 여름 대전 여행")
        private String title;
    }
    @Getter
    public static class FolderUpdateRequest {
        @Schema(example = "나의 여름 대전 여행")
        private String title;

        @Schema(example = "[1,2,3]")
        private List<Integer> sequence;
    }
}
