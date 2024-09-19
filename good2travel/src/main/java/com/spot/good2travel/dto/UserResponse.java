package com.spot.good2travel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class UserResponse {

    @Getter
    public static class UserInfoResponse{
        @Schema(description = "유저의 닉네임", example = "개구리")
        private final String nickName;
        @Schema(description = "유저가 사는 광역시/도의 이름", example = "경상북도")
        private final String metropolitanGovernmentName;
        @Schema(description = "유저의 프로필 이미지", nullable = true, example = "https://image-server.squidjiny.com/files/profile/frog.jpeg")
        private final String profileImageUrl;

        private UserInfoResponse(String nickName, String metropolitanGovernmentName,String profileImageUrl) {
            this.nickName = nickName;
            this.metropolitanGovernmentName = metropolitanGovernmentName;
            this.profileImageUrl = profileImageUrl;
        }

        public static UserInfoResponse of(String nickName, String metropolitanGovernmentName,String profileImageUrl) {
            return new UserInfoResponse(nickName, metropolitanGovernmentName, profileImageUrl);
        }
    }

}
