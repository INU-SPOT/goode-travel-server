package com.spot.good2travel.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemFolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean isFinished;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private Folder folder;

    @Builder
    public ItemFolder(Boolean isFinished, Item item, Folder folder) {
        this.isFinished = isFinished;
        this.item = item;
        this.folder = folder;
    }

    public static ItemFolder of(Item item, Folder folder) {
        if (item.getType()==ItemType.GOODE){
            return ItemFolder.builder()
                    .isFinished(null)
                    .item(item)
                    .folder(folder)
                    .build();
        } else {
            return ItemFolder.builder()
                    .isFinished(false)
                    .item(item)
                    .folder(folder)
                    .build();
        }
    }
}
