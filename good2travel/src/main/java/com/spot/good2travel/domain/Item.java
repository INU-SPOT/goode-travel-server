package com.spot.good2travel.domain;

import com.spot.good2travel.common.entity.BaseEntity;
import com.spot.good2travel.dto.FolderRequest;
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
    @Enumerated(EnumType.ORDINAL)
    private ItemType type;

    @Column(nullable = false)
    private String title;

    private String imageUrl;

    private String description;

    private String address;

    @Column(nullable = false)
    private Boolean isOfficial;

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ItemCategory> itemCategories = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ItemCourse> itemCourses = new ArrayList<>();

    @OneToMany(mappedBy = "mainItem", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Course> courses = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ItemPost> itemPosts = new ArrayList<>();

    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<ItemFolder> itemFolders = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "local_government_id")
    private LocalGovernment localGovernment;

    @Builder
    public Item(ItemType type, String title, String imageUrl, String description, String address,
                Boolean isOfficial, LocalGovernment localGovernment) {
        this.type = type;
        this.title = title;
        this.imageUrl = imageUrl;
        this.description = description;
        this.address = address;
        this.isOfficial = isOfficial;
        this.localGovernment = localGovernment;
    }

    public Item(Item item){
        this.type = item.getType();
        this.title = item.getTitle();
        this.imageUrl = item.getImageUrl();
        this.description = item.getDescription();
        this.address = item.getAddress();
        this.isOfficial = item.getIsOfficial();
        this.localGovernment = item.getLocalGovernment();
    }

    public Item copy(){
        return new Item(this);
    }


    public static Item ofGoode(ItemRequest.OfficialItemCreateRequest request, LocalGovernment localGovernment) {
        return Item.builder()
                .type(request.getType())
                .title(request.getTitle())
                .imageUrl(request.getImageUrl())
                .description(request.getDescription())
                .address(request.getAddress())
                .isOfficial(true)
                .localGovernment(localGovernment)
                .build();
    }

    public static Item ofPlan(ItemRequest.OfficialItemCreateRequest request, LocalGovernment localGovernment) {
        return Item.builder()
                .type(request.getType())
                .title(request.getTitle())
                .address(request.getAddress())
                .localGovernment(localGovernment)
                .isOfficial(true)
                .build();
    }

    public static Item of(ItemRequest.ItemCreateRequest request, LocalGovernment localGovernment) {
        return Item.builder()
                .type(request.getType())
                .title(request.getTitle())
                .imageUrl(request.getImageUrl())
                .address(request.getAddress())
                .localGovernment(localGovernment)
                .isOfficial(false)
                .build();
    }

    public void updateItem(ItemRequest.ItemUpdateRequest itemUpdateRequest, LocalGovernment localGovernment) {
        this.type = itemUpdateRequest.getType();
        this.imageUrl = itemUpdateRequest.getImageUrl();
        this.title = itemUpdateRequest.getTitle();
        this.localGovernment = localGovernment;
    }

    public void updateUserItem(FolderRequest.ItemFolderUpdateRequest request, LocalGovernment localGovernment) {
        this.title = request.getTitle();
        this.localGovernment = localGovernment;
        this.address = request.getAddress();
    }
}
