package com.spot.good2travel.controller;

import com.spot.good2travel.common.dto.CommonResponse;
import com.spot.good2travel.dto.AdvertisementRequest;
import com.spot.good2travel.service.AdvertisementService;
import com.spot.good2travel.service.ImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class AdvertisementController {

    private final AdvertisementService advertisementService;
    private final ImageService imageService;

    @PostMapping("/v1/ad")
    @Operation(
            summary = "광고용 굳이/계획 저장 (관리자용)",
            description = "광고 링크가 연결되어 있는 굳이/계획을 저장한다. (관리자용)" +
                    "<br><br> - request : accessToken 헤더 추가, AdItemCreateRequest" +
                    "<br><br> - response : Item DB 상의 pk"
    )
    public CommonResponse<?> createAdItem(@RequestBody AdvertisementRequest.AdItemCreateRequest adItemCreateRequest){
        return CommonResponse.success("광고용 굳이/계획 저장 완료", advertisementService.createAdItem(adItemCreateRequest));
    }

    @PostMapping(value = "/v1/ad/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "광고용 이미지 등록",
            description = "광고 이미지를 nginx에 등록합니다. " +
            "<br><br> - request: accessToken 헤더 추가, MultipartFile " +
            "<br><br> - response: 이미지 이름(이미지가 없으면 null 반환)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사진 등록 성공", content = @Content(schema = @Schema(implementation = String.class))),
    })
    public CommonResponse<?> uploadImage(@RequestPart(value = "file", required = false) MultipartFile file) {
        return CommonResponse.success("사진 등록 성공", imageService.uploadUserImageToNginx(file));
    }

    //그냥 광고 아이템 확인용, 아마 관광코스 조회에 광고 조회도 포함될 예정
    @GetMapping("/v1/ad/{advertisementId}")
    public CommonResponse<?> getAdItem(@PathVariable("advertisementId") Long advertisementId){
        return CommonResponse.success("광고 아이템 반환 성공", advertisementService.getAdItem(advertisementId));
    }


}
