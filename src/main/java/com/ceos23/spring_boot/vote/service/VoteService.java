package com.ceos23.spring_boot.vote.service;

import com.ceos23.spring_boot.global.exception.CustomException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import com.ceos23.spring_boot.poll.domain.Candidate;
import com.ceos23.spring_boot.poll.domain.Poll;
import com.ceos23.spring_boot.poll.repository.CandidateRepository;
import com.ceos23.spring_boot.poll.repository.PollRepository;
import com.ceos23.spring_boot.vote.dto.VoteRequest;
import com.ceos23.spring_boot.vote.dto.VoteResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VoteService {

    private final PollRepository pollRepository;
    private final CandidateRepository candidateRepository;

    public VoteService(PollRepository pollRepository, CandidateRepository candidateRepository) {
        this.pollRepository = pollRepository;
        this.candidateRepository = candidateRepository;
    }

    @Transactional
    public VoteResponse vote(Long pollId, VoteRequest request) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow(() -> new CustomException(ErrorCode.POLL_NOT_FOUND));

        Candidate candidate = candidateRepository.findById(request.getCandidateId())
                .orElseThrow(() -> new CustomException(ErrorCode.CANDIDATE_NOT_FOUND));

        if (!candidate.belongsTo(poll)) {
            throw new CustomException(ErrorCode.CANDIDATE_NOT_IN_POLL);
        }

        candidate.increaseVoteCount();

        return VoteResponse.of(poll.getId(), candidate.getId());
    }
}