package com.spot.good2travel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class CommentRequest {

    @Getter
    public static class CommentCreateUpdateRequest{

        @Schema(example = "1")
        private Long postId;

        @Schema(example = "아 ㅋㅋ 내용 개구린데요?")
        private String content;
    }

    @Getter
    public static class ReplyCommentCreateUpdateRequest {

        @Schema(example = "1")
        private Long postId;

        @Schema(example = "1")
        private Long commentId;

        @Schema(example = "신고함 ㅅㄱ")
        private String content;
    }

}
