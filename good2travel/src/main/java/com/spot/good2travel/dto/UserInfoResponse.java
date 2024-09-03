package com.spot.good2travel.dto;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
public class UserInfoResponse {

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
