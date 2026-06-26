package com.ceos23.spring_boot.poll.service;

import com.ceos23.spring_boot.global.exception.CustomException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import com.ceos23.spring_boot.poll.domain.Candidate;
import com.ceos23.spring_boot.poll.domain.Poll;
import com.ceos23.spring_boot.poll.dto.CandidateCreateRequest;
import com.ceos23.spring_boot.poll.dto.PollCreateRequest;
import com.ceos23.spring_boot.poll.dto.PollCreateResponse;
import com.ceos23.spring_boot.poll.dto.PollResultResponse;
import com.ceos23.spring_boot.poll.repository.CandidateRepository;
import com.ceos23.spring_boot.poll.repository.PollRepository;
import com.ceos23.spring_boot.poll.dto.PollListResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PollService {

    private final PollRepository pollRepository;
    private final CandidateRepository candidateRepository;

    public PollService(PollRepository pollRepository, CandidateRepository candidateRepository) {
        this.pollRepository = pollRepository;
        this.candidateRepository = candidateRepository;
    }

    @Transactional(readOnly = true)
    public List<PollListResponse> getPolls() {
        return pollRepository.findAllByOrderByIdAsc().stream()
                .map(PollListResponse::from)
                .toList();
    }

    @Transactional
    public PollCreateResponse createPoll(PollCreateRequest request) {
        Poll poll = Poll.of(request.getTitle(), request.getVoteType());
        Poll savedPoll = pollRepository.save(poll);

        List<Candidate> candidates = request.getCandidates().stream()
                .map(candidateRequest -> createCandidate(savedPoll, candidateRequest))
                .toList();

        candidateRepository.saveAll(candidates);

        return PollCreateResponse.from(savedPoll);
    }

    @Transactional(readOnly = true)
    public PollResultResponse getPollResult(Long pollId) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new CustomException(ErrorCode.POLL_NOT_FOUND));

        List<Candidate> candidates = candidateRepository.findByPollOrderByVoteCountDesc(poll);

        return PollResultResponse.of(poll, candidates);
    }

    private Candidate createCandidate(Poll poll, CandidateCreateRequest request) {
        return Candidate.of(
                poll,
                request.getName(),
                request.getPart(),
                request.getTeam()
        );
    }
}