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
        private Long folderId;
        private Long itemId;
        private String title;
        private String emoji;
    }

    @Getter
    public static class FolderUpdateRequest {
        private String title;
        private List<Long> sequence;
    }

    @Getter
    public static class ItemFolderDeleteRequest{
        private Long itemPostId;
    }
}
