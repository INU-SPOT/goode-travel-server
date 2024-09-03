package com.spot.good2travel.controller;

import com.spot.good2travel.common.dto.CommonResponse;
import com.spot.good2travel.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class UserController {

    private final UserService userService;

    @GetMapping("/v1/user/is-registered")
    @Operation(summary = "유저 가입 확인", description = "유저가 이미 가입되어 있는 유저인지 확인. <br><br> - request: accessToken을 헤더에 넣어 주세요. <br><br> - response: Boolean. true = 이미 회원가입이 된 상태. false = 회원가입이 되지 않은 상태")
    public CommonResponse<?> isRegistered(@AuthenticationPrincipal UserDetails userDetails) {
        return CommonResponse.success("회원 가입 여부 출력 성공", userService.isRegistered(userDetails));
    }

    @GetMapping("/v1/user")
    @Operation(summary = "유저 정보 조회(사진+닉네임)", description = "유저의 프로필 이미지 url과 닉네임을 조회. <br><br> - request: accessToken을 헤더에 넣어 주세요. <br><br> - response: UserInfoResponse(Dto)")
    public CommonResponse<?> userInfo(@AuthenticationPrincipal UserDetails userDetails) {
        return CommonResponse.success("유저 정보 출력 성공", userService.getUserInfo(userDetails));
    }

    @PostMapping(value = "/v1/user/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "유저 회원가입", description = "유저를 회원가입 시킵니다.(닉네임과 사진 설정 가능) <br><br> - request: <br> String nickname <br> MultipartFile image <br> AccessToken(헤더에 넣어 주세요) <br><br> - response: null")
    public CommonResponse<?> register(String nickname, @RequestPart("image")MultipartFile image, @AuthenticationPrincipal UserDetails userDetails) {
        userService.register(nickname, image, userDetails);
        return CommonResponse.success("회원가입 성공", null);
    }
}
