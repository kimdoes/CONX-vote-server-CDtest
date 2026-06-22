package com.ceos23.spring_boot.poll.dto;

import com.ceos23.spring_boot.global.type.VoteType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;

@Schema(description = "투표 생성 요청")
public class PollCreateRequest {

    @Schema(description = "투표 제목", example = "23기 데모데이 투표")
    private String title;

    @Schema(description = "투표 유형", example = "DEMO_DAY")
    private VoteType voteType;

    @Schema(description = "후보 목록")
    private List<CandidateCreateRequest> candidates;

    public String getTitle() {
        return title;
    }

    public VoteType getVoteType() {
        return voteType;
    }

    public List<CandidateCreateRequest> getCandidates() {
        return candidates;
    }
}