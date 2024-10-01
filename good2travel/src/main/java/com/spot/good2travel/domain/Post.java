package com.spot.good2travel.domain;

import com.spot.good2travel.common.entity.BaseEntity;
import com.spot.good2travel.dto.PostRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String thumbnailImageName;

    private String firstContent;

    private String lastContent;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    private Integer report;

    @ElementCollection(fetch = FetchType.LAZY)
    @OrderColumn(name = "order_index")
    private List<Long> sequence = new ArrayList<>();

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPost> itemPosts;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    private Post(String title, String thumbnailImageName, String firstContent, String lastContent, LocalDate startDate, LocalDate endDate,
                 Integer report, List<Long> sequence,List<ItemPost> itemPosts, List<Comment> comments, User user){
        this.title = title;
        this.firstContent = firstContent;
        this.lastContent = lastContent;
        this.startDate = startDate;
        this.endDate = endDate;
        this.report = report;
        this.comments = comments;
        this.sequence = sequence;
        this.itemPosts = itemPosts;
        this.user = user;
    }

    public static Post of(PostRequest.PostCreateUpdateRequest request, User user){
        return Post.builder()
                .title(request.getTitle())
                .firstContent(request.getFirstContent())
                .lastContent(request.getLastContent())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .report(0)
                .user(user)
                .build();
    }

    public Post updatePostSequenceAndImageName(List<Long> sequence, String imageUrl){
        this.sequence = sequence;
        this.thumbnailImageName = imageUrl;

        return this;
    }

    public Post updatePost(PostRequest.PostCreateUpdateRequest request, String imageName, List<Long> sequence){
        this.title = request.getTitle();
        this.thumbnailImageName = imageName;
        this.firstContent = request.getFirstContent();
        this.lastContent =request.getLastContent();
        this.startDate = request.getStartDate();
        this.endDate = request.getEndDate();
        this.sequence = sequence;
        return this;
    }

}
