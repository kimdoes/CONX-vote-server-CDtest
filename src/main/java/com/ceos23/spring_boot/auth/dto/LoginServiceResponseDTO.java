package com.ceos23.spring_boot.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 응답")
public record LoginServiceResponseDTO(
        String accessToken,
        String refreshToken,
        LoginResponseDTO loginInfo

) {
    public static LoginServiceResponseDTO of(String accessToken, String refreshToken, LoginResponseDTO info) {
        return new LoginServiceResponseDTO(accessToken, refreshToken, info);
    }
}