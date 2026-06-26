package com.ceos23.spring_boot.vote.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "내 투표 상태 응답")
public class VoteStatusResponse {

    @Schema(description = "투표 여부", example = "true")
    private final boolean hasVoted;

    @Schema(description = "내가 투표한 후보 ID", example = "1", nullable = true)
    private final Long candidateId;

    @Schema(description = "내가 투표한 후보 이름", example = "Groupeat", nullable = true)
    private final String candidateName;

    private VoteStatusResponse(boolean hasVoted, Long candidateId, String candidateName) {
        this.hasVoted = hasVoted;
        this.candidateId = candidateId;
        this.candidateName = candidateName;
    }

    public static VoteStatusResponse voted(Long candidateId, String candidateName) {
        return new VoteStatusResponse(true, candidateId, candidateName);
    }

    public static VoteStatusResponse notVoted() {
        return new VoteStatusResponse(false, null, null);
    }

    public boolean isHasVoted() {
        return hasVoted;
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public String getCandidateName() {
        return candidateName;
    }
}