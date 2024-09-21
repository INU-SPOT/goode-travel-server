package com.spot.good2travel.dto;

import com.spot.good2travel.domain.Folder;
import com.spot.good2travel.domain.Item;
import com.spot.good2travel.domain.ItemFolder;
import com.spot.good2travel.domain.ItemType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

public class FolderResponse {

    @Getter
    @Setter
    public static class FolderListResponse{
        @Schema(example = "1")
        private Long folderId;
        @Schema(example = "나의 대전 여행기")
        private String title;
        @Schema(example = "\uD83E\uDEE1")
        private String imageUrl; //해당 폴더에서 제일 처음 저장된 굳이의 사진 링크 (없을 경우 null)

        public FolderListResponse(Long folderId, String title, String imageUrl) {
            this.folderId = folderId;
            this.title = title;
            this.imageUrl = imageUrl;
        }
    }

    @Getter
    @Setter
    public static class FolderDetailResponse{
        @Schema(example = "1")
        private Long folderId;
        @Schema(example = "나의 대전 여행기")
        private String title;
        private List<ItemFolderResponse> itemFolders;

        public FolderDetailResponse(Long folderId, String title, List<ItemFolderResponse> itemFolders) {
            this.folderId = folderId;
            this.title = title;
            this.itemFolders = itemFolders;
        }

        public static FolderDetailResponse of(Folder folder, List<ItemFolderResponse> itemFolders){
            return new FolderDetailResponse(folder.getId(), folder.getTitle(), itemFolders);
        }
    }

    @Setter
    @Getter
    public static class ItemFolderResponse {
        @Schema(example = "1")
        private Long itemId;
        @Schema(example = "1")
        private Long itemFolderId;
        @Schema(example = "PLAN")
        private ItemType type;
        @Schema(example = "민규형이랑 피시방가기")
        private String title;

        private String image;
        @Schema(example = "인천광역시 부평구 뭐시기...")
        private String address;
        @Schema(example = "2024-12-19")
        private LocalDate finishDate;
        @Schema(example = "true")
        private Boolean isFinished;

        @Builder
        public ItemFolderResponse(Long itemId, Long itemFolderId, ItemType type, String title, String image,
                                  String address, LocalDate finishDate, Boolean isFinished) {
            this.itemId = itemId;
            this.itemFolderId = itemFolderId;
            this.type = type;
            this.title = title;
            this.image = image;
            this.address = address;
            this.finishDate = finishDate;
            this.isFinished = isFinished;
        }

        public static ItemFolderResponse of(Item item, ItemFolder itemFolder, Boolean isFinished, String emoji) {
            String image = (item.getType() == ItemType.PLAN && item.getImageUrl() == null) ? emoji : item.getImageUrl();

            return ItemFolderResponse.builder()
                    .itemId(item.getId())
                    .itemFolderId(itemFolder.getId())
                    .title(item.getTitle())
                    .type(item.getType())
                    .image(image)
                    .address(item.getAddress())
                    .finishDate(isFinished ? itemFolder.getFinishDate() : null)
                    .isFinished(isFinished)
                    .build();
        }
    }
}
