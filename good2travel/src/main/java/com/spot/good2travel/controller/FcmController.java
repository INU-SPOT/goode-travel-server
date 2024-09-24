package com.spot.good2travel.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import com.spot.good2travel.common.dto.CommonResponse;
import com.spot.good2travel.common.fcm.FcmRequest;
import com.spot.good2travel.service.FcmService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.Null;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class FcmController {

    private final FcmService fcmService;

    @PostMapping("/v1/fcm")
    @Operation(
            summary = "fcm 토큰 저장 및 업데이트",
            description = "fcm 토큰을 저장하거나 업데이트 합니다" +
                    "<br><br> - request : fcmUpdateDto" +
                    "<br><br> - response : x"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "토큰 저장 완료", content = @Content(schema = @Schema(implementation = Null.class))),
    })
    public ResponseEntity<CommonResponse<?>> updateToken(@RequestBody FcmRequest.FcmUpdate fcmUpdate, @AuthenticationPrincipal UserDetails userDetails){
        fcmService.updateToken(fcmUpdate, userDetails);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success("토큰 저장 완료", null));
    }

    @PostMapping("/v1/fcm/send")
    @Operation(
            summary = "fcm 전송 (테스트 용, 실제 사용은 하지 않는다)",
            description = "fcm을 전송합니다." +
                    "<br><br> - request : FcmMessageDto" +
                    "<br><br> - response : projects/goode2travel-fcm/messages/**"
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "메세지 생성 완료", content = @Content(schema = @Schema(implementation = String.class))),
    })
    public ResponseEntity<CommonResponse<?>> sendMessage(@RequestBody FcmRequest.FcmMessage fcmRequest) throws FirebaseMessagingException {
        String response = fcmService.sendMessage(fcmRequest.getFcmToken(), fcmRequest.getTitle(), fcmRequest.getBody(), 2L);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(CommonResponse.success("send message", response));
    }
}
