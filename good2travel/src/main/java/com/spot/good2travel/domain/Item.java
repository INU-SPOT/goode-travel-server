package com.spot.good2travel.domain;

import com.spot.good2travel.common.entity.BaseEntity;
import com.spot.good2travel.dto.ItemRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private String title;

    private String imageUrl;

    private String description;

    private String address;

    @Column(nullable = false)
    private Boolean isOfficial;

    private String emoji;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<ItemCategory> itemCategories = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<ItemPost> itemPosts = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL)
    private List<ItemFolder> itemFolders = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_government_id")
    private LocalGovernment localGovernment;

    @Builder
    public Item(String type, String title, String imageUrl, String description, String address,
                Boolean isOfficial, String emoji, LocalGovernment localGovernment) {
        this.type = type;
        this.title = title;
        this.imageUrl = imageUrl;
        this.description = description;
        this.address = address;
        this.isOfficial = isOfficial;
        this.emoji = emoji;
        this.localGovernment = localGovernment;
    }


    public static Item of(ItemRequest.ItemCreateRequest request, LocalGovernment localGovernment) {
        return Item.builder()
                .type(request.getType())
                .title(request.getTitle())
                .imageUrl(request.getImageUrl())
                .description(request.getDescription())
                .address(request.getAddress())
                .isOfficial(true)
                .emoji(request.getEmoji())
                .localGovernment(localGovernment)
                .build();
    }

}
