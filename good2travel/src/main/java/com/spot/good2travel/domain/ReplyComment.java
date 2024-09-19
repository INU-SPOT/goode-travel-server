package com.spot.good2travel.domain;

import com.spot.good2travel.common.entity.BaseEntity;
import com.spot.good2travel.dto.CommentRequest;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReplyComment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String content;

    private Integer report;

    private Boolean isModified;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "big_comment_id")
    private Comment comment;

    private ReplyComment(String content, Integer report, Boolean isModified, User user, Comment comment){
        this.content = content;
        this.report = report;
        this.isModified = isModified;
        this.user = user;
        this.comment = comment;
    }

    public static ReplyComment of(CommentRequest.ReplyCommentCreateUpdateRequest request, User user, Comment comment){
        return new ReplyComment(request.getContent(), 0,false, user, comment);
    }

    public ReplyComment updateReplyComment(CommentRequest.ReplyCommentCreateUpdateRequest request){
        this.content = request.getContent();
        this.isModified = true;

        return this;
    }

    public ReplyComment updateReplyCommentReport(Integer reportNum){
        this.report = reportNum;
        return this;
    }
}
