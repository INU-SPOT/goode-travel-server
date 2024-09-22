package com.spot.good2travel.domain;

import com.spot.good2travel.common.entity.BaseEntity;
import com.spot.good2travel.dto.CommentRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    private Integer report;

    private Boolean isModified;

    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL)
    private final List<ReplyComment> replyComments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id")
    private Post post;

    private Comment(String content, Integer report, Boolean isModified, User user, Post post){
        this.content = content;
        this.report = report;
        this.user = user;
        this.post = post;
        this.isModified = isModified;
    }

    public static Comment of(CommentRequest.CommentCreateRequest request, User user, Post post){
        return new Comment(request.getContent(), 0, false, user, post);
    }

    public Comment updateComment(CommentRequest.CommentUpdateRequest request){
        this.content = request.getContent();
        this.isModified = true;

        return this;
    }

    public Comment updateCommentReport(Integer reportNum){
        this.report = reportNum;
        return this;
    }
}
