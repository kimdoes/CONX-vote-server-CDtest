package com.ceos23.spring_boot.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원가입 응답")
public record SignupResponse(
        @Schema(description = "회원 ID", example = "1")
        Long userId,

        @Schema(description = "아이디", example = "user123")
        String username
) {
    public static SignupResponse of(Long userId, String username) {
        return new SignupResponse(userId, username);
    }
}