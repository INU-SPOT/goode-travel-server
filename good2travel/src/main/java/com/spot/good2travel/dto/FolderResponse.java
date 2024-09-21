package com.spot.good2travel.dto;

import com.spot.good2travel.domain.Folder;
import com.spot.good2travel.domain.Item;
import com.spot.good2travel.domain.ItemFolder;
import com.spot.good2travel.domain.ItemType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

public class FolderResponse {

    @Getter
    @Setter
    public static class FolderListResponse{
        private Long folderId;
        private String title;
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
        private Long folderId;
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
        private Long itemId;
        private Long itemFolderId;
        private ItemType type;
        private String title;
        private String image;
        private String address;
        private LocalDate finishDate;
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
                    .finishDate(isFinished ? itemFolder.getFinishTime() : null)
                    .isFinished(isFinished)
                    .build();
        }
    }
}
