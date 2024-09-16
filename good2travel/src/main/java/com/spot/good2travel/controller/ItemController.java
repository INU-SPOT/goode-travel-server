package com.spot.good2travel.controller;

import com.spot.good2travel.common.dto.CommonResponse;
import com.spot.good2travel.dto.ItemRequest;
import com.spot.good2travel.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;


    @PostMapping("/v1/goode")
    @Operation(
            summary = "시스템 상 굳이 리스트에 굳이 추가(관리자용)",
            description = "공식적인 굳이들을 추가한다." +
                    "<br><br> - request : accessToken 헤더 추가, ItemCreateRequest" +
                    "<br><br> - response : ItemCreateResponse"
    )
    public CommonResponse<?> createOfficialItem(@RequestBody ItemRequest.ItemCreateRequest itemCreateRequest){
        itemService.createOfficialItem(itemCreateRequest);
        return CommonResponse.success("관리자용 굳이 추가", null);
    }
}
