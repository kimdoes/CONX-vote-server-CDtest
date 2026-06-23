package com.ceos23.spring_boot.vote.controller;

import com.ceos23.spring_boot.global.response.ApiResponse;
import com.ceos23.spring_boot.vote.dto.VoteRequest;
import com.ceos23.spring_boot.vote.dto.VoteResponse;
import com.ceos23.spring_boot.vote.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Vote", description = "투표하기 API")
@RestController
@RequestMapping("/api/v1/polls/{pollId}/votes")
public class VoteController {

    private final VoteService voteService;

    public VoteController(VoteService voteService) {
        this.voteService = voteService;
    }

    @Operation(summary = "투표하기", description = "특정 투표의 후보에게 투표합니다.")
    @PostMapping
    public ApiResponse<VoteResponse> vote(
            @PathVariable Long pollId,
            @RequestBody VoteRequest request
    ) {
        VoteResponse response = voteService.vote(pollId, request);
        return ApiResponse.ok("투표 성공", response);
    }
}