package com.spot.good2travel.controller;

import com.spot.good2travel.common.dto.CommonResponse;
import com.spot.good2travel.dto.CommentRequest;
import com.spot.good2travel.dto.CommentResponse;
import com.spot.good2travel.service.CommentService;
import com.spot.good2travel.service.ReplyCommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class CommentController {

    private final CommentService commentService;
    private final ReplyCommentService replyCommentService;

    @GetMapping("/v1/posts/{postid}/comments")
    @Operation(summary = "게시글 댓글 가져오기", description = "게시글에서 댓글을 가져옵니다.스웨거 예시에 있는 dto를 List로 응답합니다. 멍청이슈 조심. <br><br> - request: postId <br><br> - response: List<CommentDetailsResponse>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 댓글 가져오기 성공", content = @Content(schema = @Schema(implementation = CommentResponse.CommentDetailResponse.class))),
    })
    public CommonResponse<?> isRegistered(@PathVariable Long postid, @AuthenticationPrincipal UserDetails userDetails) {
        return CommonResponse.success("댓글 가져오기 성공", commentService.getPostComments(postid, userDetails));
    }

    @PostMapping("/v1/posts/comments")
    @Operation(summary = "게시글에 댓글달기", description = "게시글에 댓글을 달겁니다. <br><br> - request: CommentCreateUpdateRequest <br> accessToken을 헤더에... <br><br> - response: null")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "게시글에 댓글 달기 성공"),
    })
    public ResponseEntity<CommonResponse<?>> addComment(@RequestBody CommentRequest.CommentCreateUpdateRequest request,
                                                       @AuthenticationPrincipal UserDetails userDetails) {
        commentService.addComment(request, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("게시글에 댓글 달기 성공", null));
    }

    @PostMapping("/v1/posts/replycomments")
    @Operation(summary = "댓글에 답글달기", description = "댓글에 답글을 달겁니다. <br><br> - request: ReplyCommentCreateUpdateRequest <br> accessToken을 헤더에... <br><br> - response: null")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "댓글에 답글달기 성공"),
    })
    public ResponseEntity<CommonResponse<?>> addReplyComment(@RequestBody CommentRequest.ReplyCommentCreateUpdateRequest request,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        replyCommentService.addReplyComment(request, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("댓글에 답글달기 성공", null));
    }

    @PatchMapping("/v1/posts/report/comments")
    @Operation(summary = "댓글신고하기", description = "댓글을 신고할겁니다. <br><br> - request: Long commentId <br> accessToken을 헤더에... <br><br> - response: null")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 신고하기 성공"),
    })
    public CommonResponse<?> reportComment(Long commentId, @AuthenticationPrincipal UserDetails userDetails) {
        commentService.reportComment(commentId, userDetails);
        return CommonResponse.success("댓글 신고하기 성공", null);
    }

    @PatchMapping("/v1/posts/report/replycomments")
    @Operation(summary = "답글신고하기", description = "답글을 신고할겁니다. <br><br> - request: Long replyCommentId <br> accessToken을 헤더에... <br><br> - response: null")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "답글 신고하기 성공"),
    })
    public CommonResponse<?> reportReplyComment(Long replyCommentId, @AuthenticationPrincipal UserDetails userDetails) {
        replyCommentService.reportReplyComment(replyCommentId, userDetails);
        return CommonResponse.success("답글 신고하기 성공", null);
    }

    @PatchMapping("/v1/posts/comments/{commentid}")
    @Operation(summary = "댓글 수정하기", description = "댓글을 수정할거에요. <br><br> - request: CommentCreateUpdateRequest <br> accessToken을 헤더에... <br><br> - response: null")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
    })
    public CommonResponse<?> updateComment(@PathVariable Long commentid,
                                             @RequestBody CommentRequest.CommentCreateUpdateRequest request,
                                             @AuthenticationPrincipal UserDetails userDetails) {
        commentService.updateComment(commentid, request, userDetails);
        return CommonResponse.success("댓글 수정 성공", null);
    }

    @PatchMapping("/v1/posts/replycomments/{replycommentid}")
    @Operation(summary = "답글 수정하기", description = "답글을 수정할거에요. <br><br> - request: ReplyCommentCreateUpdateRequest <br> accessToken을 헤더에... <br><br> - response: null")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "답글 수정 성공"),
    })
    public CommonResponse<?> updateReplyComment(@PathVariable Long replycommentid,
                                                @RequestBody CommentRequest.ReplyCommentCreateUpdateRequest request,
                                                @AuthenticationPrincipal UserDetails userDetails) {
        replyCommentService.updateReplyComment(replycommentid, request, userDetails);
        return CommonResponse.success("답글 수정 성공", null);
    }

    @DeleteMapping("/v1/posts/comments/{commentid}")
    @Operation(summary = "댓글 삭제하기", description = "댓글을 삭제할거에요. <br><br> - request: Long commentId <br> accessToken을 헤더에... <br><br> - response: null")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
    })
    public CommonResponse<?> deleteComment(@PathVariable Long commentid,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        commentService.deleteComment(commentid, userDetails);
        return CommonResponse.success("댓글 삭제하기 성공", null);
    }

    @DeleteMapping("/v1/posts/replycomments/{replycommentid}")
    @Operation(summary = "답글 삭제하기", description = "답글을 삭제할거에요. <br><br> - request: Long replycommentid <br> accessToken을 헤더에... <br><br> - response: null")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
    })
    public CommonResponse<?> deleteReplyComment(@PathVariable Long replycommentid,
                                           @AuthenticationPrincipal UserDetails userDetails) {
        replyCommentService.deleteReplyComment(replycommentid, userDetails);
        return CommonResponse.success("답글 삭제 성공", null);
    }

}
