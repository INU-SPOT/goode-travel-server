package com.spot.good2travel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.util.List;


public class FolderRequest {

    @Getter
    public static class FolderCreateRequest {
        @Schema(example = "나의 여름 대전 여행")
        @NotBlank(message = "폴더의 제목을 입력 해 주세요.")
        private String title;
    }

    @Getter
    public static class ItemFolderCreateRequest{
        @Schema(example = "1")
        @NotNull(message = "폴더의 id를 입력 해 주세요.")
        private Long folderId;
        @NotNull(message = "아이템의 id를 입력 해 주세요.")
        @Schema(example = "1")
        private Long itemId;
        @Schema(example = "🌟")
        private String emoji;
    }

    @Getter
    public static class ItemFolderUpdateRequest{
        @Schema(example = "1")
        @NotNull(message = "itemFolder의 id를 입력 해 주세요.")
        private Long itemFolderId;
        @Schema(example = "남선공원에서 노래부르기")
        private String title;
        @Schema(example = "🌟")
        private String emoji;
        @Schema(example = "1")
        private Long localGovernmentId;
        @Schema(example = "대전광역시 무슨구 무슨로 1234")
        private String address;
    }

    @Getter
    public static class FolderUpdateRequest {
        @Schema(example = "나의 여름 대전 여행")
        private String title;
        @Schema(example = "1")
        private List<Long> sequence;
    }

    @Getter
    public static class ItemFolderDeleteRequest{
        @Schema(example = "1")
        @NotNull(message = "삭제 할 itemPost의 id를 입력 해 주세요.")
        private Long itemPostId;
    }
}
