package com.ceos23.spring_boot.poll.dto;

import com.ceos23.spring_boot.global.type.VoteType;
import com.ceos23.spring_boot.poll.domain.Candidate;
import com.ceos23.spring_boot.poll.domain.Poll;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "투표 결과 응답")
public class PollResultResponse {

    @Schema(description = "투표 ID", example = "1")
    private final Long pollId;

    @Schema(description = "투표 제목", example = "23기 데모데이 투표")
    private final String title;

    @Schema(description = "투표 유형", example = "DEMO_DAY")
    private final VoteType voteType;

    @Schema(description = "후보별 결과 목록")
    private final List<CandidateResultResponse> results;

    private PollResultResponse(Long pollId, String title, VoteType voteType, List<CandidateResultResponse> results) {
        this.pollId = pollId;
        this.title = title;
        this.voteType = voteType;
        this.results = results;
    }

    public static PollResultResponse of(Poll poll, List<Candidate> candidates) {
        List<CandidateResultResponse> results = candidates.stream()
                .map(CandidateResultResponse::from)
                .toList();

        return new PollResultResponse(
                poll.getId(),
                poll.getTitle(),
                poll.getVoteType(),
                results
        );
    }

    public Long getPollId() {
        return pollId;
    }

    public String getTitle() {
        return title;
    }

    public VoteType getVoteType() {
        return voteType;
    }

    public List<CandidateResultResponse> getResults() {
        return results;
    }
}