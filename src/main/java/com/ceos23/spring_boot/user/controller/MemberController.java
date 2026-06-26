package com.ceos23.spring_boot.user.controller;

import com.ceos23.spring_boot.global.response.ApiResponse;
import com.ceos23.spring_boot.user.dto.MemberGroupResponse;
import com.ceos23.spring_boot.user.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Member", description = "멤버 관련 API")
@RestController
@RequestMapping("/api/v1/members")
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberController {

    private final MemberService memberService;

    @Operation(summary = "멤버 목록 조회", description = "멤버 목록을 파트별로 묶어서 조회합니다.")
    @GetMapping
    public ApiResponse<MemberGroupResponse> getMembers() {
        MemberGroupResponse response = memberService.getMembers();
        return ApiResponse.ok("멤버 목록 조회 성공", response);
    }
}