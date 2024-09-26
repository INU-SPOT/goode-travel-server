package com.spot.good2travel.controller;

import com.spot.good2travel.common.dto.CommonResponse;
import com.spot.good2travel.dto.NotificationResponse;
import com.spot.good2travel.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/v1/notification")
    @Operation(
            summary = "알람 내역 반환",
            description = "알림 내역들을 최신순으로 반환한다." +
                    "<br><br> - request : X" +
                    "<br><br> - response : List<NotificationResponse>"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 내역 반환 성공", content = @Content(schema = @Schema(implementation = NotificationResponse.class))),
    })
    public CommonResponse<?> getNotifications(@AuthenticationPrincipal UserDetails userDetails){
        return CommonResponse.success("알람 내역 반환 성공", notificationService.getNotifications(userDetails));
    }

    @PatchMapping("/v1/notification/confirm/{notificationId}")
    @Operation(
            summary = "알림의 확인 여부 변경",
            description = "알림의 확인 여부를 변경한다." +
                    "<br><br> - request : Notification DB 상의 pk" +
                    "<br><br> - response : 변경된 알림 확인 여부 (Boolean)"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알람 확인 여부 변경 완료", content = @Content(schema = @Schema(implementation = Boolean.class))),
    })
    public CommonResponse<?> updateConfirm(@PathVariable("notificationId") Long notificationId){
        return CommonResponse.success(("알람 확인 여부 변경 완료"), notificationService.updateConfirm(notificationId));
    }
}

