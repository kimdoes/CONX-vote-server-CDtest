package com.ceos23.spring_boot.auth.controller;

import com.ceos23.spring_boot.auth.dto.LoginRequest;
import com.ceos23.spring_boot.auth.dto.LoginResponse;
import com.ceos23.spring_boot.auth.dto.SignupRequest;
import com.ceos23.spring_boot.auth.dto.SignupResponse;
import com.ceos23.spring_boot.auth.service.AuthService;
import com.ceos23.spring_boot.global.response.ApiResponse;
import com.ceos23.spring_boot.global.security.token.TokenProvider;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Auth", description = "인증 관련 API")
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final TokenProvider tokenProvider;

    public AuthController(AuthService authService, TokenProvider tokenProvider) {
        this.authService = authService;
        this.tokenProvider = tokenProvider;
    }

    @Operation(summary = "회원가입", description = "아이디, 비밀번호, 이메일, 파트, 이름, 팀을 입력받아 회원가입합니다.")
    @PostMapping("/signup")
    public ApiResponse<SignupResponse> signup(@RequestBody SignupRequest request) {
        SignupResponse response = authService.signup(request);
        return ApiResponse.created("회원가입 성공", response);
    }

    @Operation(summary = "로그인", description = "아이디와 비밀번호로 로그인합니다.")
    @PostMapping("/login")
    public ApiResponse<?> login(@RequestBody LoginRequest request,
                                            HttpServletResponse res) {
        LoginResponse response = authService.login(request);
        tokenProvider.setToken(response.accessToken(), response.refreshToken(), res);

        return ApiResponse.ok("로그인 성공");
    }
}