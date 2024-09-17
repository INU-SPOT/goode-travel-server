package com.spot.good2travel.controller;

import com.spot.good2travel.common.dto.CommonResponse;
import com.spot.good2travel.domain.ItemType;
import com.spot.good2travel.dto.ItemRequest;
import com.spot.good2travel.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;


    @PostMapping("/v1/goode")
    @Operation(
            summary = "시스템 상 굳이 리스트에 굳이/계획 추가(관리자용)",
            description = "공식적인 굳이/계획들을 추가한다." +
                    "<br><br> - request : accessToken 헤더 추가, ItemCreateRequest" +
                    "<br> 굳이의 경우 -> type=GOODE, title, imageUrl, description, address, categories, localGovernmentId 필요" +
                    "<br> 계획의 경우 -> type=PLAN, title, address, localGovernmentId 필요" +
                    "<br><br> - response : x"
    )
    public CommonResponse<?> createOfficialItem(@RequestBody ItemRequest.OfficialItemCreateRequest officialItemCreateRequest){
        ItemType itemType = itemService.createOfficialItem(officialItemCreateRequest);
        if (itemType == ItemType.GOODE){
            return CommonResponse.success("관리자용 굳이 추가 완료", itemType);
        } else {
            return CommonResponse.success("관리자용 계획 추가 완료", itemType);
        }
    }

    @PostMapping("/v1/goodes")
    @Operation(
            summary = "사용자가 계획을 짤 때, 커뮤니티에 글을 올릴 때 새로운 계획을 추가한다.",
            description = "새로운 사용자의 굳이/계획을 추가한다." +
                    "<br><br> - request : accessToken 헤더 추가, ItemCreateRequest" +
                    "<br><br> - response : X"
    )
    public CommonResponse<?> createItem(@RequestBody ItemRequest.ItemCreateRequest itemCreateRequest){
        return CommonResponse.success("사용자의 굳이/계획 추가 완료", itemService.createItem(itemCreateRequest));
    }

    @PostMapping(value = "/v1/goodes", params = "itemId")
    @Operation(
            summary = "코스 -> 폴더 및 폴더 -> 게시글로 굳이/계획을 옮긴다.",
            description = "굳이/계획의 공식적 여부를 확인하여 공식적이라면 pk 그대로, " +
                    "공식적이지 않다면 새로운 객체를 생성하여 pk 반환" +
                    "<br><br> - request : accessToken 헤더 추가, Item(DB) 상의 pk를 담아서 전송 (URL 명시 x)" +
                    "<br><br> - response : 불러온 굳이/계획의 item(DB)상의 pk"
    )
    public CommonResponse<?> moveItem(@RequestParam(name = "itemId") Long itemId){
        return CommonResponse.success("굳이/계획 옮기기 완료", itemService.moveItem(itemId));
    }

    
}
