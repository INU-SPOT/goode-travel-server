package com.spot.good2travel.controller;

import com.spot.good2travel.common.dto.CommonResponse;
import com.spot.good2travel.dto.FolderCreateRequest;
import com.spot.good2travel.dto.FolderUpdateRequest;
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
    public CommonResponse<?> createFolder(@RequestBody FolderCreateRequest folderRequest){
        folderService.create(folderRequest);
        return CommonResponse.success("새 폴더 생성 완료",
                null);
    }

    @PutMapping("/v1/plans/{planId}")
    @Operation(
            summary = "폴더 안의 계획 수정",
            description = "사용자의 폴더 안에 있는 계획들을 Type에 따라 수정" +
                    "<br><br> - request : FolderUpdateRequest " +
                    "<br> > type 1 : 계획 제목 수정 (title), " +
                    "<br> > type 2 : 계획 순서 수정 ,planId 가 몇번째(pos)로 갔는지 " +
                    "<br> 헤더에 accessToken 추가" +
                    "<br><br> - response : " +
                    " <br> > type 1 : 변경된 제목 (title) " +
                    " <br> > type 2 : 변경된 순서 대로 planId(pk) 출력"
    )
    public CommonResponse<?> updatePlan(@PathVariable("planId") Long planId, @RequestBody FolderUpdateRequest folderUpdateRequest){
        try {
            return CommonResponse.success("계획 수정 완료", folderService.updatePlan(folderUpdateRequest, planId));
        } catch (Exception e) {
            return CommonResponse.error("계획 수정 중 에러 발생", e.getMessage());
        }
    }

}
