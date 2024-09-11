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

    @PutMapping("/v1/plans/sequence/{folderId}")
    @Operation(
            summary = "폴더안의 계획 순서 수정",
            description = "사용자의 폴더 안에 있는 계획들의 순서를 수정" +
                    "<br><br> - request : " +
                    "folderId = folder DB 상의 pk, [PlanListUpdateRequest]" +
                    "<br><br> - response :" +
                    "폴더 안 계획들의 일련의 pk"
    )
    public CommonResponse<?> updatePlanList(@PathVariable("folderId") Long folderId, @RequestBody FolderRequest.PlanListUpdateRequest planUpdateRequest) {
        return CommonResponse.success("계획 순서 수정 완료", folderService.updatePlanList(planUpdateRequest, folderId));
    }

    @PutMapping("/v1/plans/title/{folderId}")
    @Operation(
            summary = "폴더 제목 수정",
            description = "사용자의 폴더 제목 수정" +
                    "<br><br> - request : folderId = folder DB 상의 pk, [FolderTitleUpdateRequest]" +
                    "<br><br> - response : 업데이트 된 폴더 title"
    )
    public CommonResponse<?> updateFolderTitle(@PathVariable("folderId") Long folderId, @RequestBody FolderRequest.FolderTitleUpdateRequest folderUpdateRequest) {
        return CommonResponse.success("폴더 제목 수정 완료", folderService.updateFolderTitle(folderUpdateRequest, folderId));
    }

    @GetMapping("/v1/plans")
    @Operation(
            summary = "사용자의 폴더 목록 제공",
            description = "사용자의 폴더 목록을 제공" +
                    "<br><br> - request : X" +
                    "<br><br> - response : " +
                    "[FolderListResponse]" +
                    "<br> 제일 처음 저장된 굳이 imageUrl 반환" +
                    "<br> 폴더에 굳이가 없을 경우 imageUrl = null"
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
                    "<br><br> - response : [ItemListResponse]" +
                    "<br> Goode와 Plan으로 나눠 리스트 반환"
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
