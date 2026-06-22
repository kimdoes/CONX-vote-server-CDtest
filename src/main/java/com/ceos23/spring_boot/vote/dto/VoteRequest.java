package com.ceos23.spring_boot.vote.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "투표 요청")
public class VoteRequest {

    @Schema(description = "후보 ID", example = "1")
    private Long candidateId;

    public Long getCandidateId() {
        return candidateId;
    }
}