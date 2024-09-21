package com.spot.good2travel.controller;

import com.spot.good2travel.common.dto.CommonResponse;
import com.spot.good2travel.dto.AlarmResponse;
import com.spot.good2travel.service.AlarmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping("/v1/alarms")
    @Operation(
            summary = "알람 내역 반환",
            description = "알림 내역들을 최신순으로 반환한다." +
                    "<br><br> - request : X" +
                    "<br><br> - response : List<AlarmResponse>"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 내역 반환 성공", content = @Content(schema = @Schema(implementation = AlarmResponse.class))),
    })
    public CommonResponse<?> getAlarms(@AuthenticationPrincipal UserDetails userDetails){
        return CommonResponse.success("알람 내역 반환 성공", alarmService.getAlarms(userDetails));
    }
}
