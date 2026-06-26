package com.ceos23.spring_boot.vote.controller;

import com.ceos23.spring_boot.global.response.ApiResponse;
import com.ceos23.spring_boot.global.security.userDetails.CustomUserDetails;
import com.ceos23.spring_boot.vote.dto.VoteRequest;
import com.ceos23.spring_boot.vote.dto.VoteResponse;
import com.ceos23.spring_boot.vote.dto.VoteStatusResponse;
import com.ceos23.spring_boot.vote.service.VoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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
            @RequestBody VoteRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        VoteResponse response = voteService.vote(pollId, request, userDetails.getId());
        return ApiResponse.ok("투표 성공", response);
    }

    @Operation(summary = "내 투표 여부 조회", description = "현재 로그인한 사용자가 해당 투표에 투표했는지 조회합니다.")
    @GetMapping("/me")
    public ApiResponse<VoteStatusResponse> getMyVoteStatus(
            @PathVariable Long pollId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        VoteStatusResponse response = voteService.getMyVoteStatus(pollId, userDetails.getId());
        return ApiResponse.ok("내 투표 여부 조회 성공", response);
    }

    @Operation(summary = "재투표", description = "이미 투표한 후보를 다른 후보로 변경합니다.")
    @PatchMapping
    public ApiResponse<VoteResponse> revote(
            @PathVariable Long pollId,
            @RequestBody VoteRequest request,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        VoteResponse response = voteService.revote(pollId, request, userDetails.getId());
        return ApiResponse.ok("재투표 성공", response);
    }

    @Operation(summary = "투표 취소", description = "현재 로그인한 사용자의 해당 투표 기록을 취소합니다.")
    @DeleteMapping
    public ApiResponse<?> cancelVote(
            @PathVariable Long pollId,
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
        voteService.cancelVote(pollId, userDetails.getId());
        return ApiResponse.ok("투표 취소 성공");
    }
}