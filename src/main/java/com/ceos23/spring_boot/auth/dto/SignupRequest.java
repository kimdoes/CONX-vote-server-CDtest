package com.ceos23.spring_boot.auth.dto;

import com.ceos23.spring_boot.user.domain.Part;
import com.ceos23.spring_boot.user.domain.Team;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "회원가입 요청")
public record SignupRequest(
        @Schema(description = "아이디", example = "user123")
        String userId,

        @Schema(description = "비밀번호", example = "Password1234!")
        String password,

        @Schema(description = "이메일", example = "user@example.com")
        String email,

        @Schema(description = "파트", example = "BACKEND")
        Part part,

        @Schema(description = "이름", example = "홍길동")
        String username,

        @Schema(description = "팀", example = "CONX")
        Team team
) {
}