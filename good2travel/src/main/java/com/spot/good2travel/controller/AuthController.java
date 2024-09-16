package com.spot.good2travel.controller;

import com.spot.good2travel.common.dto.CommonResponse;
import com.spot.good2travel.dto.TokenResponse;
import com.spot.good2travel.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class AuthController {

    private final AuthService authService;

    @PostMapping(value = "/v1/auth/reissue")
    @Operation(summary = "토큰 재발급", description = "refreshToken을 사용해서 accessToken과 기한이 더 늘어난 RefreshToken을 재발급합니다. <br><br> - request: <br> RefreshToken(헤더에 넣어 주세요) <br><br> - response: <br> TokenResponse(Dto)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "토큰 재발급 성공", content = @Content(schema = @Schema(implementation = TokenResponse.class))),
    })
    public CommonResponse<?> reissueAccessToken(HttpServletRequest request) {
        return CommonResponse.success("토큰 재발급 성공", authService.reissueAccessToken(request));
    }

}
