package com.spot.good2travel.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemFolder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Boolean isFinished;

    private LocalDate finishTime;

    private String emoji;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    private Folder folder;

    @Builder
    public ItemFolder(Boolean isFinished,String emoji,Item item, Folder folder) {
        this.isFinished = isFinished;
        this.emoji = emoji;
        this.item = item;
        this.folder = folder;
    }

    public static ItemFolder of(String emoji, Item item, Folder folder) {
        return ItemFolder.builder()
                .emoji(emoji)
                .isFinished(Boolean.FALSE)
                .item(item)
                .folder(folder)
                .build();
    }

}
