package com.spot.good2travel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;

@Getter
public class UserRequest {
    @Getter
    public static class UserRegisterUpdateRequest{
        @Schema(example = "깨구링")
        @NotBlank(message = "서비스에서 사용 할 닉네임을 정해 주세요.")
        String nickname;

        @Schema(example = "1")
        @NotEmpty(message = "사는 지역을 골라주세요.")
        Long MetropolitanGovernmentId;

        @Schema(example = "frog.jpeg")
        @NotBlank(message = "프로필 이미지 이름은 빈 값이 될 수 없습니다.")
        String profileImageName;
    }

}
