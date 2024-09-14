package com.spot.good2travel.domain;

import com.spot.good2travel.common.entity.BaseEntity;
import com.spot.good2travel.dto.PostRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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

    private String firstContent;

    private String lastContent;

    @Column(nullable = false)
    private LocalDate startDate;

    @Column(nullable = false)
    private LocalDate endDate;

    private Integer good;

    private Integer report;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Long> sequence;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<ItemPost> itemPosts;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private List<Comment> comments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    private Post(String title, String firstContent, String lastContent, LocalDate startDate, LocalDate endDate,
                 Integer good, Integer report, List<Long> sequence,List<ItemPost> itemPosts, List<Comment> comments, User user){
        this.title = title;
        this.firstContent = firstContent;
        this.lastContent = lastContent;
        this.startDate = startDate;
        this.endDate = endDate;
        this.good = good;
        this.report = report;
        this.comments = comments;
        this.sequence = sequence;
        this.itemPosts = itemPosts;
        this.user = user;
    }

    public static Post of(PostRequest.PostCreateRequest request, User user){
        return Post.builder()
                .title(request.getTitle())
                .firstContent(request.getFirstContent())
                .lastContent(request.getLastContent())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .good(0)
                .report(0)
                .user(user)
                .build();
    }

    public Post updatePostSequence(List<Long> sequence){
        this.sequence = sequence;

        return this;
    }

}
