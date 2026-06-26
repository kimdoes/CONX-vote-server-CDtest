package com.ceos23.spring_boot.poll.dto;

import com.ceos23.spring_boot.poll.domain.Candidate;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "후보별 투표 결과 응답")
public class CandidateResultResponse {

    @Schema(description = "후보 ID", example = "1")
    private final Long candidateId;

    @Schema(description = "후보 이름", example = "박유민")
    private final String name;

    @Schema(description = "득표 수", example = "10")
    private final int voteCount;

    private CandidateResultResponse(Long candidateId, String name, int voteCount) {
        this.candidateId = candidateId;
        this.name = name;
        this.voteCount = voteCount;
    }

    public static CandidateResultResponse from(Candidate candidate) {
        return new CandidateResultResponse(
                candidate.getId(),
                candidate.getName(),
                candidate.getVoteCount()
        );
    }

    public Long getCandidateId() {
        return candidateId;
    }

    public String getName() {
        return name;
    }

    public int getVoteCount() {
        return voteCount;
    }
}