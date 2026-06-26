package com.ceos23.spring_boot.poll.dto;

import com.ceos23.spring_boot.global.type.VoteType;
import com.ceos23.spring_boot.poll.domain.Poll;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "투표 생성 응답")
public class PollCreateResponse {

    @Schema(description = "투표 ID", example = "1")
    private final Long pollId;

    @Schema(description = "투표 제목", example = "23기 데모데이 투표")
    private final String title;

    @Schema(description = "투표 유형", example = "DEMO_DAY")
    private final VoteType voteType;

    private PollCreateResponse(Long pollId, String title, VoteType voteType) {
        this.pollId = pollId;
        this.title = title;
        this.voteType = voteType;
    }

    public static PollCreateResponse from(Poll poll) {
        return new PollCreateResponse(
                poll.getId(),
                poll.getTitle(),
                poll.getVoteType()
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
}