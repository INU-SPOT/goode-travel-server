package com.spot.good2travel.controller;

import com.spot.good2travel.common.dto.CommonResponse;
import com.spot.good2travel.dto.PostRequest;
import com.spot.good2travel.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class PostController {

    private final PostService postService;

    @PostMapping("/v1/posts")
    @Operation(summary = "게시글 등록 기능", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 등록 성공", content = @Content(schema = @Schema(implementation = Long.class))),
    })
    public CommonResponse<?> createPost(@RequestBody PostRequest.PostCreateRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        return CommonResponse.success("게시글 등록 성공", postService.createPost(request, userDetails));
    }

    @GetMapping("/v1/posts")
    @Operation(summary = "게시글 목록(썸네일) 불러오기 기능", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 목록(썸네일) 불러오기 성공", content = @Content(schema = @Schema(implementation = Boolean.class))),
    })
    public CommonResponse<?> getPosts(@RequestParam(defaultValue = "0", required = false) Integer page,
                                      @RequestParam(defaultValue = "7", required = false) Integer size) {
        return CommonResponse.success("게시글 목록(썸네일) 불러오기 성공", postService.getPosts(page, size));
    }

    @GetMapping("/v1/posts/{postid}")
    @Operation(summary = "게시글 내용 조회하기", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 내용 불러오기 성공", content = @Content(schema = @Schema(implementation = Boolean.class))),
    })
    public CommonResponse<?> getPost(@PathVariable Long postid) {
        return CommonResponse.success("게시글 내용 불러오기 성공", postService.getPost(postid));
    }
}
