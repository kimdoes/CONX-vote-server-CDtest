package com.ceos23.spring_boot.poll.dto;

import com.ceos23.spring_boot.user.domain.Part;
import com.ceos23.spring_boot.user.domain.Team;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "후보 생성 요청")
public class CandidateCreateRequest {

    @Schema(description = "후보 이름", example = "박유민")
    private String name;

    @Schema(description = "파트장 투표 후보의 파트", example = "FRONTEND", nullable = true)
    private Part part;

    @Schema(description = "데모데이 투표 후보 팀", example = "CONX", nullable = true)
    private Team team;

    public String getName() {
        return name;
    }

    public Part getPart() {
        return part;
    }

    public Team getTeam() {
        return team;
    }
}