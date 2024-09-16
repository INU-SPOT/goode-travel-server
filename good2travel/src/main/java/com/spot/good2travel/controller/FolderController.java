package com.spot.good2travel.controller;

import com.spot.good2travel.common.dto.CommonResponse;
import com.spot.good2travel.dto.FolderRequest;
import com.spot.good2travel.service.FolderService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
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
                    "<br><br> - response : null"
    )
    public CommonResponse<?> createFolder(@RequestBody FolderRequest.FolderCreateRequest folderRequest){
        folderService.create(folderRequest);
        return CommonResponse.success("새 폴더 생성 완료",
                null);
    }

    @PutMapping("/v1/plans/{folderId}")
    @Operation(
            summary = "폴더 안의 계획 제목 및 순서 수정",
            description = "사용자의 폴더 안에 있는 계획들을 Type에 따라 수정" +
                    "<br><br> - request : itemId = folder DB상의 pk, FolderUpdateRequest, 헤더에 accessToken 추가"+
                    "<br><br> - response : FolderUpdateResponse"
    )
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
    public CommonResponse<?> getFolderList(){
        //todo 유저 정보 필요
        return CommonResponse.success("폴더 목록 반환 성공",
                folderService.getFolderList());
    }

    @GetMapping("/v1/plans/{folderId}")
    @Operation(
            summary = "폴더 안의 계획 목록 제공",
            description = "폴더 안의 계획 목록을 제공한다." +
                    "<br><br> - request : folderId - Folder DB 상의 PK" +
                    "<br><br> - response : ItemListResponse"
    )
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
    public CommonResponse<?> deleteFolder(@PathVariable("folderId") Long folderId){
        folderService.deleteFolder(folderId);
        return CommonResponse.success("폴더 삭제 성공", null);
    }
}
