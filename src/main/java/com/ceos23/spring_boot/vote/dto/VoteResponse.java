package com.ceos23.spring_boot.vote.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "투표 응답")
public class VoteResponse {

    @Schema(description = "투표 ID", example = "1")
    private final Long pollId;

    @Schema(description = "후보 ID", example = "1")
    private final Long candidateId;

    @Schema(description = "후보 이름", example = "Groupeat")
    private final String candidateName;

    private VoteResponse(Long pollId, Long candidateId, String candidateName) {
        this.pollId = pollId;
        this.candidateId = candidateId;
        this.candidateName = candidateName;
    }

    public static VoteResponse of(Long pollId, Long candidateId, String candidateName) {
        return new VoteResponse(pollId, candidateId, candidateName);
    }

    public Long getPollId() {
        return pollId;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }
}