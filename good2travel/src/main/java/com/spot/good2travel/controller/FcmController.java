package com.spot.good2travel.controller;

import com.spot.good2travel.common.dto.CommonResponse;
import com.spot.good2travel.common.fcm.dto.FcmRequest;
import com.spot.good2travel.service.FcmService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class FcmController {

    private final FcmService fcmService;

    @PostMapping("/v1/fcm")
    public ResponseEntity<CommonResponse<?>> updateToken(@RequestBody FcmRequest.FcmUpdateDto fcmUpdateDto){
        String response = fcmService.updateToken(fcmUpdateDto);
        return ResponseEntity.ok()
                .body(CommonResponse.success("update fcmToken", response));
    }

    @PostMapping("/v1/fcm/send")
    public ResponseEntity<CommonResponse<?>> sendMessage(@RequestBody FcmRequest fcmRequest) throws IOException {
        String response = fcmService.sendMessageTo(fcmRequest);
        return ResponseEntity.ok()
                .body(CommonResponse.success("send message", response));
    }
}
