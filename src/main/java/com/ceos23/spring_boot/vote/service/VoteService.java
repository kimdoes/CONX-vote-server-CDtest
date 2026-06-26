package com.ceos23.spring_boot.vote.service;

import com.ceos23.spring_boot.global.exception.CustomException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import com.ceos23.spring_boot.global.type.VoteType;
import com.ceos23.spring_boot.poll.domain.Candidate;
import com.ceos23.spring_boot.poll.domain.Poll;
import com.ceos23.spring_boot.poll.repository.CandidateRepository;
import com.ceos23.spring_boot.poll.repository.PollRepository;
import com.ceos23.spring_boot.user.domain.Member;
import com.ceos23.spring_boot.user.repository.MemberRepository;
import com.ceos23.spring_boot.vote.domain.Vote;
import com.ceos23.spring_boot.vote.dto.VoteRequest;
import com.ceos23.spring_boot.vote.dto.VoteResponse;
import com.ceos23.spring_boot.vote.dto.VoteStatusResponse;
import com.ceos23.spring_boot.vote.repository.VoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VoteService {

    private final PollRepository pollRepository;
    private final CandidateRepository candidateRepository;
    private final MemberRepository memberRepository;
    private final VoteRepository voteRepository;

    public VoteService(
            PollRepository pollRepository,
            CandidateRepository candidateRepository,
            MemberRepository memberRepository,
            VoteRepository voteRepository
    ) {
        this.pollRepository = pollRepository;
        this.candidateRepository = candidateRepository;
        this.memberRepository = memberRepository;
        this.voteRepository = voteRepository;
    }

    @Transactional
    public VoteResponse vote(Long pollId, VoteRequest request, Long memberId) {
        Poll poll = getPoll(pollId);
        Member member = getMember(memberId);
        Candidate candidate = getCandidate(request.getCandidateId());

        validateCandidateInPoll(candidate, poll);
        validateVoteRule(poll, candidate, member);

        if (voteRepository.existsByMemberAndPoll(member, poll)) {
            throw new CustomException(ErrorCode.ALREADY_VOTED);
        }

        candidate.increaseVoteCount();

        Vote vote = Vote.of(member, poll, candidate);
        voteRepository.save(vote);

        return VoteResponse.of(poll.getId(), candidate.getId(), candidate.getName());
    }

    @Transactional(readOnly = true)
    public VoteStatusResponse getMyVoteStatus(Long pollId, Long memberId) {
        Poll poll = getPoll(pollId);
        Member member = getMember(memberId);

        return voteRepository.findByMemberAndPoll(member, poll)
                .map(vote -> VoteStatusResponse.voted(
                        vote.getCandidate().getId(),
                        vote.getCandidate().getName()
                ))
                .orElseGet(VoteStatusResponse::notVoted);
    }

    @Transactional
    public VoteResponse revote(Long pollId, VoteRequest request, Long memberId) {
        Poll poll = getPoll(pollId);
        Member member = getMember(memberId);
        Candidate newCandidate = getCandidate(request.getCandidateId());

        validateCandidateInPoll(newCandidate, poll);
        validateVoteRule(poll, newCandidate, member);

        Vote vote = voteRepository.findByMemberAndPoll(member, poll)
                .orElseThrow(() -> new CustomException(ErrorCode.VOTE_NOT_FOUND));

        Candidate oldCandidate = vote.getCandidate();

        if (oldCandidate.getId().equals(newCandidate.getId())) {
            return VoteResponse.of(poll.getId(), newCandidate.getId(), newCandidate.getName());
        }

        oldCandidate.decreaseVoteCount();
        newCandidate.increaseVoteCount();
        vote.changeCandidate(newCandidate);

        return VoteResponse.of(poll.getId(), newCandidate.getId(), newCandidate.getName());
    }

    @Transactional
    public void cancelVote(Long pollId, Long memberId) {
        Poll poll = getPoll(pollId);
        Member member = getMember(memberId);

        Vote vote = voteRepository.findByMemberAndPoll(member, poll)
                .orElseThrow(() -> new CustomException(ErrorCode.VOTE_NOT_FOUND));

        Candidate candidate = vote.getCandidate();
        candidate.decreaseVoteCount();

        voteRepository.delete(vote);
    }

    private Poll getPoll(Long pollId) {
        return pollRepository.findById(pollId)
                .orElseThrow(() -> new CustomException(ErrorCode.POLL_NOT_FOUND));
    }

    private Member getMember(Long memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
    }

    private Candidate getCandidate(Long candidateId) {
        return candidateRepository.findById(candidateId)
                .orElseThrow(() -> new CustomException(ErrorCode.CANDIDATE_NOT_FOUND));
    }

    private void validateCandidateInPoll(Candidate candidate, Poll poll) {
        if (!candidate.belongsTo(poll)) {
            throw new CustomException(ErrorCode.CANDIDATE_NOT_IN_POLL);
        }
    }

    private void validateVoteRule(Poll poll, Candidate candidate, Member member) {
        if (poll.getVoteType() == VoteType.PART_LEADER) {
            validatePartLeaderVote(candidate, member);
            return;
        }

        if (poll.getVoteType() == VoteType.DEMO_DAY) {
            validateDemoDayVote(candidate, member);
        }
    }

    private void validatePartLeaderVote(Candidate candidate, Member member) {
        if (candidate.getPart() == null || member.getPart() == null) {
            throw new CustomException(ErrorCode.INVALID_PART_LEADER_VOTE);
        }

        if (!candidate.getPart().name().equals(member.getPart().name())) {
            throw new CustomException(ErrorCode.INVALID_PART_LEADER_VOTE);
        }
    }

    private void validateDemoDayVote(Candidate candidate, Member member) {
        if (candidate.getTeam() == null || member.getTeam() == null) {
            throw new CustomException(ErrorCode.INVALID_DEMO_DAY_VOTE);
        }

        if (candidate.getTeam().name().equalsIgnoreCase(member.getTeam().name())) {
            throw new CustomException(ErrorCode.INVALID_DEMO_DAY_VOTE);
        }
    }
}