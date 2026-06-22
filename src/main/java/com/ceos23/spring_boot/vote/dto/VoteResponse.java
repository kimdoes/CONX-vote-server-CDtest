package com.ceos23.spring_boot.vote.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "투표 응답")
public class VoteResponse {

    @Schema(description = "투표 ID", example = "1")
    private final Long pollId;

    @Schema(description = "후보 ID", example = "1")
    private final Long candidateId;

    private VoteResponse(Long pollId, Long candidateId) {
        this.pollId = pollId;
        this.candidateId = candidateId;
    }

    public static VoteResponse of(Long pollId, Long candidateId) {
        return new VoteResponse(pollId, candidateId);
    }

    public Long getPollId() {
        return pollId;
    }

    public Long getCandidateId() {
        return candidateId;
    }
}