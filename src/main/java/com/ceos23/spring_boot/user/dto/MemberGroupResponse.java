package com.ceos23.spring_boot.user.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "파트별 멤버 목록 응답")
public record MemberGroupResponse(

        @Schema(description = "프론트엔드 멤버 목록")
        List<MemberResponse> frontend,

        @Schema(description = "백엔드 멤버 목록")
        List<MemberResponse> backend
) {
    public static MemberGroupResponse of(List<MemberResponse> frontend, List<MemberResponse> backend) {
        return new MemberGroupResponse(frontend, backend);
    }
}