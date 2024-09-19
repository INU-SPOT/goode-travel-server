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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class FolderController {

    private final FolderService folderService;

    @PostMapping("/v1/plans")
    @Operation(
            summary = "새 폴더 생성",
            description = "사용자의 계획을 저장할 새 폴더 생성 " +
                    "<br><br> - request : FolderCreateRequest, 헤더에 accessToken 추가" +
                    "<br><br> - response : 생성된 폴더 이름"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "새 폴더 생성 완료", content = @Content(schema = @Schema(implementation = String.class))),
    })
    public CommonResponse<?> createFolder(@RequestBody FolderRequest.FolderCreateRequest folderRequest, @AuthenticationPrincipal UserDetails userDetails){
        return CommonResponse.success("새 폴더 생성 완료",
                folderService.create(folderRequest , userDetails));
    }

    @PutMapping("/v1/plans/{folderId}")
    @Operation(
            summary = "폴더 제목 수정 및 폴더 안의 계획들 생성 및 순서 수정",
            description = "폴더 제목을 수정하거나 폴더 안의 계획들을 생성 또는 수정한다." +
                    "<br><br> - request : itemId = folder DB상의 pk, FolderUpdateRequest, 헤더에 accessToken 추가"+
                    "<br><br> - response : FolderUpdateResponse"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "폴더 수정 완료", content = @Content(schema = @Schema(implementation = FolderResponse.FolderUpdateResponse.class))),
    })
    public CommonResponse<?> updatePlanList(@PathVariable("folderId") Long folderId, @RequestBody FolderRequest.FolderUpdateRequest planUpdateRequest) {
        return CommonResponse.success("폴더 수정 완료", folderService.updatePlanList(planUpdateRequest, folderId));
    }

    @GetMapping("/v1/plans")
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
        return CommonResponse.success("폴더 목록 반환 성공",
                folderService.getFolderList(userDetails));
    }

    @GetMapping("/v1/plans/{folderId}")
    @Operation(
            summary = "폴더 안의 계획 목록 제공",
            description = "폴더 안의 계획 목록을 제공한다." +
                    "<br><br> - request : folderId - Folder DB 상의 PK" +
                    "<br><br> - response : List<FolderResponse.ItemResponse>"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "폴더 안의 계획 리스트 반환 성공", content = @Content(schema = @Schema(implementation = FolderResponse.ItemResponse.class))),
    })
    public CommonResponse<?> getFolderItemList(@PathVariable("folderId") Long folderId){
        return CommonResponse.success("폴더 안의 계획 리스트 반환 성공"
                    ,folderService.getItemList(folderId));

    }

    @DeleteMapping("/v1/plans/{folderId}")
    @Operation(
            summary = "폴더 삭제",
            description = "폴더를 삭제한다." +
                    "<br><br> - request : folderId - Folder DB 상의 PK" +
                    "<br><br> - response : X"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "폴더 삭제 성공", content = @Content(schema = @Schema(implementation = Null.class))),
    })
    public CommonResponse<?> deleteFolder(@PathVariable("folderId") Long folderId){
        folderService.deleteFolder(folderId);
        return CommonResponse.success("폴더 삭제 성공", null);
    }
}
