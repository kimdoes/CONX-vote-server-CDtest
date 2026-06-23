package com.ceos23.spring_boot.poll.dto;

import com.ceos23.spring_boot.global.type.Part;
import com.ceos23.spring_boot.global.type.Team;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "후보 생성 요청")
public class CandidateCreateRequest {

    @Schema(description = "후보 이름", example = "DiggIndie")
    private String name;

    @Schema(description = "파트", example = "BACKEND", nullable = true)
    private Part part;

    @Schema(description = "팀", example = "DIGG_INDIE", nullable = true)
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