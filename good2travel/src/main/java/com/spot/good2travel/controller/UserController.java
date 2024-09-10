package com.spot.good2travel.controller;

import com.spot.good2travel.common.dto.CommonResponse;
import com.spot.good2travel.dto.UserRequest.UserRegisterUpdateRequest;
import com.spot.good2travel.dto.UserResponse;
import com.spot.good2travel.service.ImageService;
import com.spot.good2travel.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
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
    private final ImageService imageService;

    @GetMapping("/v1/users/is-registered")
    @Operation(summary = "유저 가입 확인", description = "유저가 이미 가입되어 있는 유저인지 확인. <br><br> - request: accessToken을 헤더에 넣어 주세요. <br><br> - response: Boolean. true = 이미 회원가입이 된 상태. false = 회원가입이 되지 않은 상태")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원 가입 여부 출력 성공", content = @Content(schema = @Schema(implementation = Boolean.class))),
    })
    public CommonResponse<?> isRegistered(@AuthenticationPrincipal UserDetails userDetails) {
        return CommonResponse.success("회원 가입 여부 출력 성공", userService.isRegistered(userDetails));
    }

    @GetMapping("/v1/users")
    @Operation(summary = "유저 정보 조회(사진+닉네임)", description = "유저의 프로필 이미지 url과 닉네임을 조회. <br><br> - request: accessToken을 헤더에 넣어 주세요. <br><br> - response: UserResponse(Dto)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "유저 정보 출력 성공", content = @Content(schema = @Schema(implementation = UserResponse.UserInfoResponse.class))),
    })
    public CommonResponse<?> userInfo(@AuthenticationPrincipal UserDetails userDetails) {
        return CommonResponse.success("유저 정보 출력 성공", userService.getUserInfo(userDetails));
    }

    @PutMapping(value = "/v1/users")
    @Operation(summary = "유저 가입(기본정보수정)", description = "유저를 회원가입(기본정보수정) 시킵니다.(닉네임과 사는지역 설정 가능) <br><br> - request: <br> UserRegisterUpdateRequest <br> AccessToken(헤더에 넣어 주세요) <br><br> - response: UserInfoResponse")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원가입 성공", content = @Content(schema = @Schema(implementation = UserResponse.UserInfoResponse.class))),
    })
    public CommonResponse<?> userUpdateRegister(@RequestBody @Valid UserRegisterUpdateRequest request, @AuthenticationPrincipal UserDetails userDetails) {
        return CommonResponse.success("기본정보수정 성공", userService.userRegisterUpdate(request, userDetails));
    }

    @PostMapping(value = "/v1/users/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "유저 프로필 이미지 등록", description = "유저의 프로필 이미지를 nginx에 등록합니다. <br><br> - request: <br> MultipartFile <br> AccessToken(헤더에 넣어 주세요) <br><br> - response: 이미지 이름(이미지가 없으면 null 반환)")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "사진 등록 성공", content = @Content(schema = @Schema(implementation = String.class))),
    })
    public CommonResponse<?> uploadImage(@RequestPart(value = "file", required = false) MultipartFile file) {
        return CommonResponse.success("사진 등록 성공", imageService.uploadUserImageToNginx(file));
    }

}
