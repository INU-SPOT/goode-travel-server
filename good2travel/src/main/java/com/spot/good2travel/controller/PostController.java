package com.spot.good2travel.controller;

import com.spot.good2travel.common.dto.CommonResponse;
import com.spot.good2travel.dto.PostRequest;
import com.spot.good2travel.dto.PostResponse;
import com.spot.good2travel.service.ImageService;
import com.spot.good2travel.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class PostController {

    private final PostService postService;
    private final ImageService imageService;

    @PostMapping("/v1/posts")
    @Operation(summary = "게시글 등록 기능", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 등록 성공", content = @Content(schema = @Schema(implementation = Long.class))),
    })
    public CommonResponse<?> createPost(@RequestBody PostRequest.PostCreateUpdateRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        return CommonResponse.success("게시글 등록 성공", postService.createPost(request, userDetails));
    }

    @GetMapping("/v1/posts")
    @Operation(summary = "게시글 목록(썸네일) 불러오기 기능", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 목록(썸네일) 불러오기 성공", content = @Content(schema = @Schema(implementation = PostResponse.PostThumbnailResponse.class))),
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
    public CommonResponse<?> getPost(@PathVariable Long postid, @AuthenticationPrincipal UserDetails userDetails) {
        return CommonResponse.success("게시글 내용 불러오기 성공", postService.getPost(postid, userDetails));
    }

    @PostMapping(value = "/v1/posts/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "게시글 이미지 등록", description = "게시글의 프로필 이미지를 nginx에 등록합니다. <br><br> - request: <br> MultipartFile <br> AccessToken(헤더에 넣어 주세요) <br><br> - response: 이미지 이름(이미지가 없으면 null 반환)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사진 등록 성공", content = @Content(schema = @Schema(implementation = String.class))),
    })
    public CommonResponse<?> uploadImage(@RequestPart(value = "file", required = false) MultipartFile file) {
        return CommonResponse.success("사진 등록 성공", imageService.uploadUserImageToNginx(file));
    }

    @PostMapping(value = "/v1/posts/{postid}/good")
    @Operation(summary = "조하요~ 누르기", description = "게시글에 좋아요 누르기/해제하기 <br><br> - request: <br> Long postid <br> AccessToken(헤더에 넣어 주세요) <br><br> - response: 해당 기능 수행 후 좋아요 수")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사진 등록 성공", content = @Content(schema = @Schema(implementation = String.class))),
    })
    public CommonResponse<?> updateGood(@PathVariable Long postid, @AuthenticationPrincipal UserDetails userDetails) {
        return CommonResponse.success("사진 등록 성공", postService.updateGoodNum(postid, userDetails));
    }

    @PatchMapping("/v1/posts/{postid}")
    @Operation(summary = "게시글 수정하기", description = "")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 수정하기 성공", content = @Content(schema = @Schema(implementation = Long.class))),
    })
    public CommonResponse<?> updatePost(@PathVariable Long postid, @RequestBody PostRequest.PostCreateUpdateRequest request,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        return CommonResponse.success("게시글 내용 불러오기 성공", postService.updatePost(postid, request, userDetails));
    }

}
