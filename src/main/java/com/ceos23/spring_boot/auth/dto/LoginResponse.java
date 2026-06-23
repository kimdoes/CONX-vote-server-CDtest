package com.ceos23.spring_boot.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Schema(description = "로그인 응답")
public record LoginResponse(
        @Schema(description = "Access Token", example = "sample-access-token")
        String accessToken,

        @Schema(description = "Refresh Token", example = "sample-refresh-token")
        String refreshToken

) {
    public static LoginResponse of(String accessToken, String refreshToken) {
        return new LoginResponse(accessToken, refreshToken);
    }
}