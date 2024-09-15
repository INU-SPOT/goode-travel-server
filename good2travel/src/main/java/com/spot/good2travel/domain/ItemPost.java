package com.spot.good2travel.domain;

import com.spot.good2travel.dto.PostRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToMany(mappedBy ="itemPost",cascade = CascadeType.ALL)
    private List<ItemPostImage> itemPostImages;

    @Builder
    public ItemPost(String content, Item item, Post post, List<ItemPostImage> itemPostImages){
        this.content = content;
        this.item = item;
        this.post = post;
        this.itemPostImages = itemPostImages;
    }

    public static ItemPost of(PostRequest.ItemPostCreateUpdateRequest request, Item item, Post post){
        return ItemPost.builder()
                .content(request.getContent())
                .item(item)
                .post(post).build();
    }

    public ItemPost updateItemPost(PostRequest.ItemPostCreateUpdateRequest request){
        this.content = request.getContent();
        this.item = item;
        this.post = post;

        return this;
    }
}
