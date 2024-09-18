package com.spot.good2travel.dto;

import com.spot.good2travel.domain.Item;
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

    @Setter
    @Getter
    public static class ItemResponse {
        private Long id;
        private ItemType type;
        private String title;
        private String image;
        private String address;
        private LocalDate createDate;
        private Boolean isFinished;

        @Builder
        public ItemResponse(Long id, ItemType type, String title, String image, String address, LocalDate createDate, Boolean isFinished) {
            this.id = id;
            this.type = type;
            this.title = title;
            this.image = image;
            this.address = address;
            this.createDate = createDate;
            this.isFinished = isFinished;
        }

        public static ItemResponse of(Item item, Boolean isFinished) {
            String image = (item.getType() == ItemType.PLAN && item.getImageUrl() == null)
                    ? item.getEmoji()
                    : item.getImageUrl();

            return ItemResponse.builder()
                    .id(item.getId())
                    .title(item.getTitle())
                    .type(item.getType())
                    .image(image)
                    .address(item.getAddress())
                    .createDate(item.getCreateDate().toLocalDate())
                    .isFinished(isFinished)
                    .build();
        }
    }
}
