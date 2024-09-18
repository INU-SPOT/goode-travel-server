package com.spot.good2travel.domain;

import com.spot.good2travel.dto.PostRequest;
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
public class ItemPost {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    @ElementCollection(fetch = FetchType.LAZY)
    @OrderColumn(name = "order_index")
    private List<Long> sequence = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    @OneToMany(mappedBy ="itemPost",cascade = CascadeType.ALL)
    private List<ItemPostImage> itemPostImages;

    @Builder
    public ItemPost(String content, Item item, Post post, List<Long> sequence, List<ItemPostImage> itemPostImages){
        this.content = content;
        this.item = item;
        this.post = post;
        this.sequence = sequence;
        this.itemPostImages = itemPostImages;
    }

    public static ItemPost of(PostRequest.ItemPostCreateUpdateRequest request, Item item, Post post){
        return ItemPost.builder()
                .content(request.getContent())
                .item(item)
                .post(post).build();
    }

    public ItemPost updateItemPost(PostRequest.ItemPostCreateUpdateRequest request, List<Long> sequence, Item item, Post post){
        this.content = request.getContent();
        this.sequence = sequence;
        this.item = item;
        this.post = post;

        return this;
    }

    public ItemPost updateSequence(List<Long> sequence){
        this.sequence = sequence;

        return this;
    }
}
