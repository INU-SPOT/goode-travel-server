package com.spot.good2travel.controller;

import com.spot.good2travel.common.dto.CommonResponse;
import com.spot.good2travel.service.AlarmService;
import io.swagger.v3.oas.annotations.Operation;
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
            description = ""
    )
    public CommonResponse<?> getAlarms(@AuthenticationPrincipal UserDetails userDetails){
        return CommonResponse.success("알람 내역 반환 성공", alarmService.getAlarms(userDetails));
    }
}
