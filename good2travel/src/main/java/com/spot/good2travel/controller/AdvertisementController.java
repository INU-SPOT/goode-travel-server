package com.spot.good2travel.controller;

import com.spot.good2travel.common.dto.CommonResponse;
import com.spot.good2travel.dto.AdvertisementRequest;
import com.spot.good2travel.service.AdvertisementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class AdvertisementController {

    private final AdvertisementService advertisementService;

    @PostMapping("/v1/ad")
    @Operation(
            summary = "광고용 굳이/계획 저장 (관리자용)",
            description = "광고 링크가 연결되어 있는 굳이/계획을 저장한다. (관리자용)" +
                    "<br><br> - request : accessToken 헤더 추가, AdItemCreateUpdateRequest" +
                    "<br><br> - response : Advertisement DB 상의 pk"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "광고용 굳이/계획 저장 완료", content = @Content(schema = @Schema(implementation = Long.class))),
    })
    public CommonResponse<?> createAdItem(@RequestBody AdvertisementRequest.AdCreateUpdateRequest adCreateUpdateRequest){
        return CommonResponse.success("광고용 굳이/계획 저장 완료", advertisementService.createAdItem(adCreateUpdateRequest));
    }

    @PutMapping("/v1/ad/{adId}")
    @Operation(
            summary = "광고용 굳이/계획 수정 (관리자용)",
            description = "광고용 굳이/계획을 수정한다. (관리자용)" +
                    "<br><br> - request : accessToken 헤더 추가, AdItemCreateUpdateRequest" +
                    "<br><br> - response : Advertisement DB 상의 pk "
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "광고용 굳이/계획 수정 완료", content = @Content(schema = @Schema(implementation = Long.class))),
    })
    public CommonResponse<?> updateAdItem(@PathVariable("adId") Long adId, @RequestBody AdvertisementRequest.AdCreateUpdateRequest adCreateUpdateRequest){
        return CommonResponse.success("광고용 굳이/계획 수정 완료", advertisementService.updateAdItem(adId, adCreateUpdateRequest));
    }

    @DeleteMapping("/v1/ad/{adId}")
    @Operation(
            summary = "광고용 굳이/계획 삭제 (관리자용)",
            description = "광고용 굳이/계획을 삭제한다. (관리자용)" +
                    "<br><br> - request: accessToken 헤더 추가, Advertisement DB 상의 pk" +
                    "<br><br> - response : X "
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "광고용 굳이/계획 삭제 완료", content = @Content(schema = @Schema(implementation = Null.class))),
    })
    public CommonResponse<?> deleteAdItem(@PathVariable("adId") Long adId){
        advertisementService.deleteAdItem(adId);
        return CommonResponse.success("광고용 굳이/계획 삭제 완료", null);
    }
}
