package com.spot.good2travel.controller;

import com.spot.good2travel.common.dto.CommonResponse;
import com.spot.good2travel.dto.FolderRequest;
import com.spot.good2travel.dto.FolderResponse;
import com.spot.good2travel.service.FolderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class FolderController {

    private final FolderService folderService;

    @PostMapping("/v1/folders")
    @Operation(
            summary = "새 폴더 생성",
            description = "사용자의 계획을 저장할 새 폴더 생성 " +
                    "<br><br> - request : FolderCreateRequest, 헤더에 accessToken 추가" +
                    "<br><br> - response : Folder DB 상의 PK"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "새 폴더 생성 완료", content = @Content(schema = @Schema(implementation = Long.class))),
    })
    public ResponseEntity<CommonResponse<?>> createFolder(@RequestBody FolderRequest.FolderCreateRequest folderRequest,
                                                          @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("새 폴더 생성 완료",
                folderService.create(folderRequest , userDetails)));
    }

    /*
    리스트로 한번에 여러개 생성 할 수 있는 api가 필요할듯 함.
     */

    @PostMapping("/v1/folders/plan")
    @Operation(
            summary = "새 계획 생성",
            description = "사용자의 폴더에 (커스텀, 공식) 계획 추가 " +
                    "<br><br> - request : FolderCreateRequest, 헤더에 accessToken 추가" +
                    "<br><br> - response : Folder DB 상의 PK"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "새 계획 생성 완료", content = @Content(schema = @Schema(implementation = Long.class))),
    })
    public ResponseEntity<CommonResponse<?>> createFolderPlan(@RequestBody FolderRequest.ItemFolderCreateRequest request,
                                                              @AuthenticationPrincipal UserDetails userDetails){
        return ResponseEntity.status(HttpStatus.CREATED).body(CommonResponse.success("새 계획 생성 완료",
                folderService.createItemFolder(request , userDetails)));
    }

    @PatchMapping("/v1/folders/{folderId}")
    @Operation(
            summary = "폴더 제목 , 계획 순서 수정",
            description = "폴더 제목과 계획 순서를 수정한다. 제목, 계획 순서 중 사용자가 변경을 한 값만 전송 해 주세요. ex) 제목을 변경하였으나 순서는 변경하지 않았으면: title에는 변경된 값을, 순서에는 null을 반환해주세요." +
                    "<br><br> - request : itemId = folder DB상의 pk, FolderCreateRequest, 헤더에 accessToken 추가"+
                    "<br><br> - response : Folder DB 상의 PK"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "폴더 수정 완료", content = @Content(schema = @Schema(implementation = Long.class))),
    })
    public CommonResponse<?> updateFolder(@PathVariable("folderId") Long folderId, @RequestBody FolderRequest.FolderUpdateRequest planUpdateRequest,
                                          @AuthenticationPrincipal UserDetails userDetails) {
        return CommonResponse.success("폴더 수정 완료", folderService.updateFolder(planUpdateRequest, folderId, userDetails));
    }

    @GetMapping("/v1/folders")
    @Operation(
            summary = "사용자의 폴더 목록 제공",
            description = "사용자의 폴더 목록을 제공" +
                    "<br><br> - request : X" +
                    "<br><br> - response : FolderListResponse"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "폴더 목록 반환 성공", content = @Content(schema = @Schema(implementation = FolderResponse.FolderListResponse.class))),
    })
    public CommonResponse<?> getFolderList(@AuthenticationPrincipal UserDetails userDetails){
        return CommonResponse.success("폴더 목록 반환 성공", folderService.getFolderList(userDetails));
    }

    @GetMapping("/v1/folders/{folderId}")
    @Operation(
            summary = "폴더 세부정보 제공",
            description = "폴더의 이름과 폴더의 계획 목록을 제공한다." +
                    "<br><br> - request : folderId - Folder DB 상의 PK" +
                    "<br><br> - response : FolderDetailResponse"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "폴더 세부 정보 반환 성공", content = @Content(schema = @Schema(implementation = FolderResponse.FolderDetailResponse.class))),
    })
    public CommonResponse<?> getFolderItemList(@PathVariable("folderId") Long folderId){
        return CommonResponse.success("폴더 세부 정보 반환 성공", folderService.getFolderDetail(folderId));
    }

    @DeleteMapping("/v1/folders/{folderId}")
    @Operation(
            summary = "폴더 삭제",
            description = "폴더를 삭제한다." +
                    "<br><br> - request : folderId - Folder DB 상의 PK" +
                    "<br><br> - response : X"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "폴더 삭제 성공", content = @Content(schema = @Schema(implementation = Null.class))),
    })
    public CommonResponse<?> deleteFolder(@PathVariable("folderId") Long folderId, @AuthenticationPrincipal UserDetails userDetails){
        folderService.deleteFolder(folderId, userDetails);
        return CommonResponse.success("폴더 삭제 성공", null);
    }

    @DeleteMapping("/v1/folders/{folderId}/plan/{itempostid}")
    @Operation(
            summary = "폴더 안의 계획 삭제",
            description = "폴더 안의 계획을 삭제한다." +
                    "<br><br> - request : folderId - Folder DB 상의 PK" +
                    "<br><br> - response : Folder DB 상의 PK"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계획 삭제 성공", content = @Content(schema = @Schema(implementation = Long.class))),
    })
    public CommonResponse<?> deleteFolder(@PathVariable("folderId") Long folderId, @PathVariable("itempostid") Long itemPostId,
                                          @AuthenticationPrincipal UserDetails userDetails){
        return CommonResponse.success("폴더 삭제 성공", folderService.deleteItemFolder(folderId, itemPostId, userDetails));
    }

    @PutMapping("/v1/folders/plan/{itempostid}")
    @Operation(
            summary = "계획이 끝난 여부 변환하기",
            description = "계획이 끝난 여부를 변환합니다." +
                    "<br><br> - request : itemFolderId - itemFolder DB 상의 PK" +
                    "<br><br> - response : 변환된 후의 계획이 끝난 여부(Boolean)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "계획 완료여부 변환 성공", content = @Content(schema = @Schema(implementation = Boolean.class))),
    })
    public CommonResponse<?> switchPlanFinished(@PathVariable("itempostid") Long itemPostId){
        return CommonResponse.success("계획 완료여부 변환 성공", folderService.switchIsFinished(itemPostId));
    }

}
