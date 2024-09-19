package com.spot.good2travel.domain;

import com.spot.good2travel.dto.PostRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemPostImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imageName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_post_id")
    private ItemPost itemPost;

    @Builder
    public ItemPostImage(String imageName, ItemPost itemPost){
        this.imageName = imageName;
        this.itemPost = itemPost;
    }

    public static ItemPostImage of(PostRequest.ItemPostImageRequest itemPostImageRequest, ItemPost itemPost){
        return ItemPostImage.builder()
                .imageName(itemPostImageRequest.getImageName())
                .itemPost(itemPost)
                .build();
    }

    public ItemPostImage updateItemPostImage(PostRequest.ItemPostImageRequest itemPostImageRequest){
        this.imageName = itemPostImageRequest.getImageName();

        return this;
    }

}
