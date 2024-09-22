package com.spot.good2travel.controller;

import com.spot.good2travel.common.dto.CommonResponse;
import com.spot.good2travel.domain.ItemType;
import com.spot.good2travel.dto.ItemRequest;
import com.spot.good2travel.dto.ItemResponse;
import com.spot.good2travel.service.ItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;


    @PostMapping("/v1/item")
    @Operation(
            summary = "시스템 상 굳이 리스트에 굳이/계획 추가(관리자용)",
            description = "공식적인 굳이/계획들을 추가한다." +
                    "<br><br> - request : accessToken 헤더 추가, OfficialItemCreateRequest" +
                    "<br> 굳이의 경우 -> itemType=GOODE, title, image, description, address, categories, localGovernmentId 필요" +
                    "<br> 계획의 경우 -> itemType=PLAN, title, address, localGovernmentId 필요" +
                    "<br><br> - response : 추가한 Item의 ItemType(GOODE , PLAN)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "관리자용 굳이/계획 추가 완료", content = @Content(schema = @Schema(implementation = ItemType.class)))
    })
    public ResponseEntity<CommonResponse<?>> createOfficialItem(@RequestBody ItemRequest.OfficialItemCreateRequest officialItemCreateRequest){
        return ResponseEntity.status(HttpStatus.CREATED
        ).body(CommonResponse.success("관리자용 굳이/계획 추가 완료", itemService.createOfficialItem(officialItemCreateRequest)));
    }

    @PostMapping("/v1/items")
    @Operation(
            summary = "사용자가 새로운 계획을 추가한다.",
            description = "사용자가 계획을 짤 때, 커뮤니티에 글을 올릴 때 새로운 사용자의 굳이/계획을 추가." +
                    "<br><br> - request : accessToken 헤더 추가, " +
                    "ItemCreateRequest - 폴더에서 추가하면 emoji, 커뮤니티에서 추가하면 image 첨부" +
                    "<br><br> - response : 추가한 굳이/계획의 DB 상 pk"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자의 굳이/계획 추가 완료", content = @Content(schema = @Schema(implementation = Long.class)))
    })
    public CommonResponse<?> createItem(@RequestBody ItemRequest.ItemCreateRequest itemCreateRequest){
        return CommonResponse.success("사용자의 굳이/계획 추가 완료", itemService.createItem(itemCreateRequest));
    }

    @PostMapping(value = "/v1/items/move", params = "itemId")
    @Operation(
            summary = "코스 -> 폴더 및 폴더 -> 게시글로 굳이/계획을 가져온다.",
            description = "굳이/계획의 공식적 여부를 확인하여 공식적이라면 pk 그대로, " +
                    "공식적이지 않다면 새로운 객체를 생성하여 pk 반환" +
                    "<br><br> - request : accessToken 헤더 추가, Item(DB) 상의 pk를 body에 담아서 전송 (URL에 명시 x)" +
                "<br><br> - response : 가져온 굳이/계획의 item(DB)상의 pk"
        )
        @ApiResponses(value = {
                @ApiResponse(responseCode = "200", description = "굳이/계획 가져오기 완료", content = @Content(schema = @Schema(implementation = Long.class)))
        })
        public CommonResponse<?> moveItem(@RequestParam(name = "itemId") Long itemId){
            return CommonResponse.success("굳이/계획 가져오기 완료", itemService.moveItem(itemId));
    }

    @PutMapping("/v1/items/{itemId}")
    @Operation(summary = "사용자 굳이/계획 수정",
            description = "사용자의 굳이/계획을 수정한다." +
                    "<br><br> - request : accessToken 헤더 추가, Item(DB) 상의 pk, " +
                    "ItemUpdateRequest - 폴더에서 수정하면 emoji, 커뮤니티에서 수정하면 image 첨부" +
                    "<br><br> - response : 수정된 굳이/계획의 Item(DB) 상의 pk"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자의 굳이/계획 수정 완료", content = @Content(schema = @Schema(implementation = Long.class)))
    })
    public CommonResponse<?> updateItem(@PathVariable("itemId") Long itemId, @RequestBody ItemRequest.ItemUpdateRequest itemUpdateRequest){
        return CommonResponse.success("사용자의 굳이/계획 수정 완료", itemService.updateItem(itemId, itemUpdateRequest));
    }

    @GetMapping("/v1/items/{itemId}")
    @Operation(summary = "굳이의 상세 정보 제공",
            description = "공식적인 굳이의 상제 정보들을 제공한다." +
                    "<br><br> - request : itemId= item(DB) 상 pk" +
                    "<br><br> - response : GoodeDetailsResponse"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "굳이의 상제 정보 제공 완료", content = @Content(schema = @Schema(implementation = ItemResponse.GoodeDetailsResponse.class)))
    })
    public CommonResponse<?> getGoodeDetails(@PathVariable("itemId") Long itemId){
        return CommonResponse.success("굳이의 상제 정보 제공 완료", itemService.getGoodeDetails(itemId));
    }

    @DeleteMapping("/v1/users/items/{itemId}")
    @Operation(summary = "사용자의 굳이/계획 삭제",
        description = "사용자의 굳이/계획을 삭제한다." +
                "<br><br> - request : itemId = item(DB) 상 pk" +
                "<br><br> - response : x"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자의 굳이/계획 삭제 완료", content = @Content(schema = @Schema(implementation = Null.class)))
    })
    public CommonResponse<?> deleteUserItem(@PathVariable("itemId") Long itemId){
        itemService.deleteUserItem(itemId);
        return CommonResponse.success("사용자의 굳이/계획 삭제 완료", null);
    }

    @DeleteMapping("/v1/items/{itemId}")
    @Operation(summary = "공식/비공식 굳이/계획 삭제(관리자용)",
            description = "사용자의 굳이/계획을 삭제한다.(관리자용)" +
                    "<br><br> - request : itemId = item(DB) 상 pk" +
                    "<br><br> - response : x"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사용자의 굳이/계획 삭제 완료", content = @Content(schema = @Schema(implementation = Null.class)))
    })
    public CommonResponse<?> deleteItem(@PathVariable("itemId") Long itemId){
        itemService.deleteItem(itemId);
        return CommonResponse.success("사용자의 굳이/계획 삭제 완료", null);
    }

    //todo 굳이 관광코스 조회는 관광코스 설계 후 제작
}
