package com.ceos23.spring_boot.poll.controller;

import com.ceos23.spring_boot.global.response.ApiResponse;
import com.ceos23.spring_boot.poll.dto.PollCreateRequest;
import com.ceos23.spring_boot.poll.dto.PollCreateResponse;
import com.ceos23.spring_boot.poll.dto.PollResultResponse;
import com.ceos23.spring_boot.poll.service.PollService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Poll", description = "투표 생성 및 결과 조회 API")
@RestController
@RequestMapping("/api/v1/polls")
public class PollController {

    private final PollService pollService;

    public PollController(PollService pollService) {
        this.pollService = pollService;
    }

    @Operation(summary = "투표 만들기", description = "투표 제목, 투표 유형, 후보 목록을 입력받아 투표를 생성합니다.")
    @PostMapping
    public ApiResponse<PollCreateResponse> createPoll(@RequestBody PollCreateRequest request) {
        PollCreateResponse response = pollService.createPoll(request);
        return ApiResponse.created("투표 생성 성공", response);
    }

    @Operation(summary = "결과보기", description = "특정 투표의 후보별 득표 수를 내림차순으로 조회합니다.")
    @GetMapping("/{pollId}/results")
    public ApiResponse<PollResultResponse> getPollResult(@PathVariable Long pollId) {
        PollResultResponse response = pollService.getPollResult(pollId);
        return ApiResponse.ok("투표 결과 조회 성공", response);
    }
}