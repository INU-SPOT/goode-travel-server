package com.spot.good2travel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class UserRequest {
    @Getter
    public static class UserRegisterUpdateRequest{
        @Schema(example = "깨구링")
        @NotBlank(message = "서비스에서 사용 할 닉네임을 정해 주세요.")
        private String nickname;

        @Schema(example = "1")
        @NotNull(message = "사는 지역을 골라주세요.")
        private Long metropolitanGovernmentId;

        @Schema(example = "frog.jpeg")
        private String profileImageName;
    }

}
