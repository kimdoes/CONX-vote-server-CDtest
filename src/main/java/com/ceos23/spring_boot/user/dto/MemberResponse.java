package com.ceos23.spring_boot.user.dto;

import com.ceos23.spring_boot.user.domain.Member;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "멤버 응답")
public record MemberResponse(

        @Schema(description = "사용자 이름", example = "홍길동")
        String username,

        @Schema(description = "파트", example = "BACKEND")
        String part,

        @Schema(description = "팀", example = "CONX")
        String team
) {
    public static MemberResponse from(Member member) {
        return new MemberResponse(
                member.getUsername(),
                member.getPart().name(),
                member.getTeam().name()
        );
    }
}