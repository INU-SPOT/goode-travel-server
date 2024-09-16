package com.spot.good2travel.dto;

import com.spot.good2travel.domain.Item;
import com.spot.good2travel.domain.ItemType;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

public class FolderResponse {

    @Getter
    @Setter
    public static class FolderListResponse{
        private String title;
        private String imageUrl; //해당 폴더에서 제일 처음 저장된 굳이의 사진 링크 (없을 경우 null)

        public FolderListResponse(String title, String imageUrl) {
            this.title = title;
            this.imageUrl = imageUrl;
        }
    }

    @Getter
    @Setter
    public static class FolderUpdateResponse{
        private String title;
        private List<Integer> sequence;

        public FolderUpdateResponse(String title, List<Integer> sequence) {
            this.title = title;
            this.sequence = sequence;
        }
    }

    @Getter
    @Setter
    public static class ItemListResponse {
        List<FolderItem> folderItemList;

        public ItemListResponse(List<FolderItem> folderItems) {
            this.folderItemList = folderItems;
        }
    }

    @Setter
    @Getter
    public static class FolderItem {
        private Long id;
        private ItemType type;
        private String title;
        private String imageUrl;
        private String address;
        private LocalDate createDate;
        private Boolean isFinished;

        public FolderItem of(Item item){
            this.id = item.getId();
            this.type = item.getType();
            this.title = item.getTitle();
            if (item.getType() == ItemType.GOODE){
                this.imageUrl = item.getImageUrl()==null? item.getEmoji() : item.getImageUrl();
                this.address = item.getAddress();
                this.createDate = item.getCreateDate().toLocalDate();
                this.isFinished = null;

            } else if (item.getType() == ItemType.PLAN) {
                this.imageUrl = item.getEmoji();
                this.address = item.getAddress();
                this.createDate = item.getCreateDate().toLocalDate();
                this.isFinished = false;
            }

            return this;
        }
    }

}
