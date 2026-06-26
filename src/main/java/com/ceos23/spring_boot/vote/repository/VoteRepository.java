package com.ceos23.spring_boot.vote.repository;

import com.ceos23.spring_boot.poll.domain.Poll;
import com.ceos23.spring_boot.user.domain.Member;
import com.ceos23.spring_boot.vote.domain.Vote;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VoteRepository extends JpaRepository<Vote, Long> {

    Optional<Vote> findByMemberAndPoll(Member member, Poll poll);

    boolean existsByMemberAndPoll(Member member, Poll poll);
}