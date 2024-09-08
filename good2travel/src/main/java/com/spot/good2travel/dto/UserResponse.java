package com.spot.good2travel.dto;

import com.nimbusds.openid.connect.sdk.UserInfoResponse;
import lombok.Getter;

@Getter
public class UserResponse {

    public static class UserInfoResponse{
        private String nickName;

        private String profileImageUrl;

        private UserInfoResponse(String nickName, String profileImageUrl) {
            this.nickName = nickName;
            this.profileImageUrl = profileImageUrl;
        }

        public static UserInfoResponse of(String nickName, String profileImageUrl) {
            return new UserInfoResponse(nickName, profileImageUrl);
        }
    }

}
