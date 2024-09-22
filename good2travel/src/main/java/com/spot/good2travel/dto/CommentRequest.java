package com.spot.good2travel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CommentRequest {

    @Getter
    public static class CommentCreateUpdateRequest{

        @Schema(example = "1")
        @NotNull(message = "댓글을 작성할 post의 id를 입력해주세요.")
        private Long postId;

        @Schema(example = "아 ㅋㅋ 내용 개구린데요?")
        @NotBlank(message = "댓글 내용을 입력해주세요.")
        private String content;
    }

    @Getter
    public static class ReplyCommentCreateUpdateRequest {

        @Schema(example = "1")
        @NotNull(message = "답글을 작성할 comment의 id를 입력해주세요.")
        private Long commentId;

        @Schema(example = "신고함 ㅅㄱ")
        @NotBlank(message = "답글의 내용을 작성해주세요.")
        private String content;
    }

}
