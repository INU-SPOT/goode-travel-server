package com.spot.good2travel.dto;

import com.spot.good2travel.domain.Comment;
import com.spot.good2travel.domain.ReplyComment;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
public class CommentResponse {

    @Getter
    public static class CommentDetailResponse{

        @Schema(example = "1")
        private final Long commentId;
        @Schema(example = "1")
        private final Long userId;
        @Schema(example = "지이니")
        private final String nickname;
        @Schema(example = "frog.jpeg")
        private final String profileImageName;
        @Schema(example = "2024-04-12")
        private final LocalDate date;
        @Schema(example = "true")
        private final Boolean isOwner;
        @Schema(example = "false")
        private final Boolean isModified;
        @Schema(example = "완전 럭키비키자나?")
        private final String content;
        private final List<ReplyCommentResponse> replyComments;

        @Builder(access = AccessLevel.PRIVATE)
        public CommentDetailResponse(Long commentId, Long userId, String nickname, String profileImageName, LocalDate date,
                                     Boolean isOwner, Boolean isModified, String content, List<ReplyCommentResponse> replyComments) {
            this.commentId = commentId;
            this.userId = userId;
            this.nickname = nickname;
            this.profileImageName = profileImageName;
            this.date = date;
            this.isOwner = isOwner;
            this.isModified = isModified;
            this.content = content;
            this.replyComments = replyComments;
        }

        public static CommentDetailResponse of(Comment comment, Boolean isOwner, List<ReplyCommentResponse> replyComments){
            return CommentDetailResponse.builder()
                    .commentId(comment.getId())
                    .userId(comment.getUser().getId())
                    .nickname(comment.getUser().getNickname())
                    .profileImageName(comment.getUser().getProfileImageName())
                    .date(comment.getUpdateDate().toLocalDate())
                    .isOwner(isOwner)
                    .isModified(comment.getIsModified())
                    .content(comment.getContent())
                    .replyComments(replyComments).build();
        }
    }

    @Getter
    public static class ReplyCommentResponse{
        @Schema(example = "1")
        private final Long replyCommentId;
        @Schema(example = "1")
        private final Long userId;
        @Schema(example = "2mingyu")
        private final String nickname;
        @Schema(example = "frog.jpeg")
        private final String profileImageName;
        @Schema(example = "2024-11-28")
        private final LocalDate date;
        @Schema(example = "false")
        private final Boolean isOwner;
        @Schema(example = "true")
        private final Boolean isModified;
        @Schema(example = "이상한댓글 달지 마세요;;")
        private final String content;

        @Builder(access = AccessLevel.PRIVATE)
        public ReplyCommentResponse(Long replyCommentId, Long userId, String nickname, String profileImageName, LocalDate date,
                                     Boolean isOwner, Boolean isModified, String content) {
            this.replyCommentId = replyCommentId;
            this.userId = userId;
            this.nickname = nickname;
            this.profileImageName = profileImageName;
            this.date = date;
            this.isOwner = isOwner;
            this.isModified = isModified;
            this.content = content;
        }

        public static ReplyCommentResponse of(ReplyComment comment, Boolean isOwner){
            return ReplyCommentResponse.builder()
                    .replyCommentId(comment.getId())
                    .userId(comment.getUser().getId())
                    .nickname(comment.getUser().getNickname())
                    .profileImageName(comment.getUser().getProfileImageName())
                    .date(comment.getUpdateDate().toLocalDate())
                    .isOwner(isOwner)
                    .isModified(comment.getIsModified())
                    .content(comment.getContent())
                    .build();
        }
    }

    @Getter
    public static class UserCommentResponse{

        @Schema(example = "replyComment")
        private final String type;
        @Schema(example = "1")
        private final Long postId;
        @Schema(example = "환기리의 인천여행")
        private final String postTitle;
        @Schema(example = "2024-11-28")
        private final LocalDate date;
        @Schema(example = "true")
        private final Boolean isModified;
        private final LocalDateTime createDate;
        @Schema(example = "이상한댓글 달지 마세요;;")
        private final String content;

        @Builder
        public UserCommentResponse(String type, Long postId, String postTitle, LocalDate date, Boolean isModified, LocalDateTime createDate, String content) {
            this.type = type;
            this.postId = postId;
            this.postTitle = postTitle;
            this.date = date;
            this.isModified = isModified;
            this.createDate = createDate;
            this.content = content;
        }

        public static UserCommentResponse of(String type, Long postId, String postTitle, LocalDate date, Boolean isModified, LocalDateTime createDate,String content){
            return UserCommentResponse.builder()
                    .type(type)
                    .postId(postId)
                    .postTitle(postTitle)
                    .date(date)
                    .isModified(isModified)
                    .createDate(createDate)
                    .content(content)
                    .build();
        }
    }

}
