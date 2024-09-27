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
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class PostController {

    private final PostService postService;
    private final ImageService imageService;

    @PostMapping("/v1/posts")
    @Operation(summary = "게시글 등록 기능", description =
            "제 생각에는 여기서 실수가 제일 많이 나지 않을까 싶습니다...\n\n PostCreateUpdateRequest(post 테이블에 해당하는 부분): <br> 생성/수정의 요청에서 다른 점이 없습니다. \n\n ItemPostCreateUpdateRequest(ItemPost에 해당하는 부분. 현 dto에는 itemPosts 리스트로 명시되어 있음): <br> 게시글 생성: itemId, content, images가 필요합니다. <br> 게시글 수정: itemPostId, itemId, content, images가 필요합니다.\n\n itemPostId는 post를 수정 할 때만 필요한 값입니다. 게시글 생성할때는 null로 주시면 됩니다.\n\n만약 게시글을 수정할 때 유저가 게시글에 새로운 계획 박스를 생성했다면 itemPostId를 null로 주시길 바랍니다." +"\n\n ItemPostImageRequest(itemPost의 이미지테이블에 해당함. 현 Dto에는 images 리스트로 명시되어 있음.): <br> 게시글 생성: (사진이 있다면) imageName이 필요합니다. <br> 게시글 수정 imagePostImageId와 imageName이 필요합니다.\n\n" +
            "해줘 프론트아저씨!\n\n 아 참고로 게시글 등록 api랑 게시글 수정 api는 별개입니다. 이해를 위해 한번에 설명합니다.\n\n 반환값: postId(pk)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "게시글 등록 성공", content = @Content(schema = @Schema(implementation = Long.class))),
    })
    public ResponseEntity<CommonResponse<?>> createPost(@RequestBody @Valid PostRequest.PostCreateUpdateRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success("게시글 등록 성공", postService.createPost(request, userDetails)));
    }

    @GetMapping("/v1/posts")
    @Operation(summary = "게시글 목록(썸네일) 불러오기 + 전체 게시글 검색 기능 **페이징**", description = "게시글 목록을 불러옵니다. + 검색어에 따라 게시글을 필터링합니다. <br><br> - request: <br> 페이징 인수는 디폴트값이 설정되어 있어서 page를 넘길때만 page값을 넘겨주시면 될 것 같습니다. <br> List String localGovernments : 지역구 이름 (ex. 구로구. 서울특별시안됨ㅡㅡ 무조건 구 시 군만)<br> List String categories : 카테고리 이름(ex. 먹거리) <br> String keyword : 검색어 <br><br> - response: PostThumbnailResponse")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 목록(썸네일) 불러오기 성공", content = @Content(schema = @Schema(implementation = PostResponse.PostThumbnailResponse.class))),
    })
    public CommonResponse<?> getPosts(@RequestParam(required = false) List<Long> metropolitanGovernments,
                                      @RequestParam(required = false) List<Long> localGovernments,
                                      @RequestParam(required = false) List<String> categories,
                                      @RequestParam(required = false) String keyword,
                                      @RequestParam(defaultValue = "0", required = false) Integer page,
                                      @RequestParam(defaultValue = "7", required = false) Integer size) {
        return CommonResponse.success("게시글 목록(썸네일) 불러오기 성공", postService.searchPosts(metropolitanGovernments, localGovernments, categories, keyword, page, size));
    }

    @GetMapping("/v1/posts/{postid}")
    @Operation(summary = "게시글 내용 조회하기", description = "게시글의 세부 내용을 조회합니다. <br><br> - request: <br> Long postId <br> AccessToken(헤더에 넣어 주세요) <br><br> - response: PostDetailResponse(Dto")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 내용 불러오기 성공", content = @Content(schema = @Schema(implementation = PostResponse.PostDetailResponse.class))),
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
    @Operation(summary = "조하요~ 누르기", description = "게시글에 좋아요 누르기/해제하기 <br><br> - request: <br> Long postid <br> AccessToken(헤더에 넣어 주세요) <br><br> - response: <br> 해당 기능 수행 후 좋아요 수 <br> 토큰이 없으면 예외가 떠요~")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요 누르기/해제하기 성공", content = @Content(schema = @Schema(implementation = Long.class))),
    })
    public CommonResponse<?> updateGood(@PathVariable Long postid, @AuthenticationPrincipal UserDetails userDetails) {
        return CommonResponse.success("좋아요 누르기/해제하기 성공", postService.updateLikeNum(postid, userDetails));
    }

    @PatchMapping("/v1/posts/{postid}")
    @Operation(summary = "게시글 수정하기", description = "PostCreateUpdateRequest(post 테이블에 해당하는 부분): <br> 생성/수정의 요청에서 다른 점이 없습니다. \n\n ItemPostCreateUpdateRequest(ItemPost에 해당하는 부분. 현 dto에는 itemPosts 리스트로 명시되어 있음): <br> 게시글 생성: itemId, content, images가 필요합니다. <br> 게시글 수정: itemPostId, itemId, content, images가 필요합니다.\n\n itemPostId는 post를 수정 할 때만 필요한 값입니다. 게시글 생성할때는 null로 주시면 됩니다.\n\n만약 게시글을 수정할 때 유저가 게시글에 새로운 계획 박스를 생성했다면 itemPostId를 null로 주시길 바랍니다.\n\n ItemPostImageRequest(itemPost의 이미지테이블에 해당함. 현 Dto에는 images 리스트로 명시되어 있음.): <br> 게시글 생성: (사진이 있다면) imageName이 필요합니다. <br> 게시글 수정 imagePostImageId와 imageName이 필요합니다.\n\n" +
            "            \n\n 아 참고로 게시글 등록 api랑 게시글 수정 api는 별개입니다. 이해를 위해 한번에 설명합니다 \n\n 반환값: postId(pk)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 수정하기 성공", content = @Content(schema = @Schema(implementation = Long.class))),
    })
    public CommonResponse<?> updatePost(@PathVariable Long postid, @RequestBody @Valid PostRequest.PostCreateUpdateRequest request,
                                        @AuthenticationPrincipal UserDetails userDetails) {
        return CommonResponse.success("게시글 수정하기 성공", postService.updatePost(postid, request, userDetails));
    }

    @GetMapping("/v1/posts/top-like")
    @Operation(summary = "좋아요가 가장 많은 글 불러오기", description = "좋아요가 가장 많은 게시글을 하나 불러옵니다. <br><br> - request: <br> 음슴~ <br><br> - response: <br> TopPostResponse <br> Dto 헷갈리는 변수 설명: itemType: 0은 굳이, 1은 계획. <br> topType: 좋아요가 많은 글인지 조회수가 많은 글인지 : like, visit로 구분. <br> topNum: topType가 like면 좋아요 갯수, visit면 조회수")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "좋아요가 가장 많은 게시글 불러오기 성공", content = @Content(schema = @Schema(implementation = PostResponse.TopPostResponse.class))),
    })
    public CommonResponse<?> getTopLikePost() {
        return CommonResponse.success("좋아요가 가장 많은 게시글 불러오기 성공", postService.getTopLikePost());
    }

    @GetMapping("/v1/posts/top-visit")
    @Operation(summary = "조회수가 가장 많은 글 불러오기", description = "조회수가가 가장 많은 게시글을 하나 불러옵니다. <br><br> - request: <br> 음슴~ <br><br> - response: <br> TopPostResponse <br> Dto 헷갈리는 변수 설명: itemType: 0은 굳이, 1은 계획. <br> topType: 좋아요가 많은 글인지 조회수가 많은 글인지 : like, visit로 구분. <br> topNum: topType가 like면 좋아요 갯수, visit면 조회수")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "조회수가 가장 많은 게시글 불러오기 성공", content = @Content(schema = @Schema(implementation = PostResponse.TopPostResponse.class))),
    })
    public CommonResponse<?> getTopVisitPost() {
        return CommonResponse.success("조회수가 가장 많은 게시글 불러오기 성공",postService.getTopVisitPost());
    }

    @GetMapping("/v1/users/posts")
    @Operation(summary = "유저가 쓴 글 불러오기 **페이징**", description = "유저가 쓴 글들을 불러옵니다. <br><br> - request: <br> accessToken을 주오... <br><br> - response: <br> List<PostThumbnailResponse>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저가 쓴 글 불러오기 불러오기 성공", content = @Content(schema = @Schema(implementation = PostResponse.TopPostResponse.class))),
    })
    public CommonResponse<?> getUserPosts(@RequestParam(defaultValue = "0", required = false) Integer page,
                                          @RequestParam(defaultValue = "7", required = false) Integer size,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        return CommonResponse.success("유저가 쓴 글 불러오기 성공",postService.getUserPosts(page, size, userDetails));
    }

    @GetMapping("/v1/users/posts/like")
    @Operation(summary = "유저가 좋아요 누른 글 불러오기 **페이징**", description = "유저가 조하요~ 한 글들을 불러옵니다. <br><br> - request: <br> accessToken을 주오... <br><br> - response: <br> List<PostThumbnailResponse>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저가 좋아요 누른 글 불러오기 성공", content = @Content(schema = @Schema(implementation = PostResponse.TopPostResponse.class))),
    })
    public CommonResponse<?> getUserLikePosts(@RequestParam(defaultValue = "0", required = false) Integer page,
                                          @RequestParam(defaultValue = "7", required = false) Integer size,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        return CommonResponse.success("유저가 좋아요 누른 글 불러오기 성공",postService.getUserLikePosts(page, size, userDetails));
    }

    @DeleteMapping("/v1/posts/{postid}")
    @Operation(summary = "게시글 삭제하기", description = "게시글을 삭제해봅시다 <br><br> - request: <br> accessToken을 주오... <br> Long <br><br> - response: <br> List<PostThumbnailResponse>")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "게시글 삭제 성공"),
    })
    public CommonResponse<?> deletePost(@PathVariable Long postid, @AuthenticationPrincipal UserDetails userDetails) {
        postService.deletePost(postid, userDetails);
        return CommonResponse.success("게시글 삭제 성공", null);
    }
}