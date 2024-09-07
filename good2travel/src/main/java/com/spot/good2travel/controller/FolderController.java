package com.spot.good2travel.controller;

import com.spot.good2travel.common.dto.CommonResponse;
import com.spot.good2travel.dto.FolderCreateRequest;
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
            summary = "폴더 생성",
            description = "사용자의 계획을 저장할 새 폴더 생성 " +
                    "<br><br> request : FolderRequest.FolderCreate, 헤더에 accessToken 추가" +
                    "<br><br> response : null"
    )
    public CommonResponse<?> createFolder(@RequestBody FolderCreateRequest folderRequest){
        folderService.create(folderRequest);
        return CommonResponse.success("새 폴더 생성 완료",
                null);
    }

}
