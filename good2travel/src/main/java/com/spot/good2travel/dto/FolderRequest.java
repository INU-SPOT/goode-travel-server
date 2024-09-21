package com.spot.good2travel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.util.List;


public class FolderRequest {

    @Getter
    public static class FolderCreateRequest {
        @Schema(example = "나의 여름 대전 여행")
        private String title;
    }

    @Getter
    public static class ItemFolderCreateRequest{
        @Schema(example = "1")
        private Long folderId;
        @Schema(example = "1")
        private Long itemId;
        @Schema(example = "\\uD83E\\uDEE1")
        private String emoji;
    }

    @Getter
    public static class FolderUpdateRequest {
        @Schema(example = "나의 여름 대전 여행")
        private String title;
        private List<Long> sequence;
    }

    @Getter
    public static class ItemFolderDeleteRequest{
        @Schema(example = "1")
        private Long itemPostId;
    }
}
