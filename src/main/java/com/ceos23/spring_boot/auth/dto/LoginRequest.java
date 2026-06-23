package com.ceos23.spring_boot.auth.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "로그인 요청")
public record LoginRequest(
        @Schema(description = "아이디", example = "user123")
        String userLoginId,

        @Schema(description = "비밀번호", example = "Password1234!")
        String password
) {
}