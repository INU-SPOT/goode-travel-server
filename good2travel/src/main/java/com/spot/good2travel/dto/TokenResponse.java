package com.spot.good2travel.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
public class TokenResponse {

    @Schema(example = "djfoiqwejfiopjqweoifjiosjdfiodajfoiqcewjiofjqwoifjqioejfoieqjo")
    private final String refreshToken;

    @Schema(example = "fioiqiewfiojewiofcjeoifmjqwiocfjmiowpjfmoqwipwjcfoijoiqwjfcoipjcoipmjoijfcopqwjcpo")
    private final String accessToken;

    @Builder
    private TokenResponse(String refreshToken, String accessToken) {
        this.refreshToken = refreshToken;
        this.accessToken = accessToken;
    }

}
