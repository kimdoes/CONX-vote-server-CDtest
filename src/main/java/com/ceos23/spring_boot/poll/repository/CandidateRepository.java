package com.ceos23.spring_boot.poll.repository;

import com.ceos23.spring_boot.poll.domain.Candidate;
import com.ceos23.spring_boot.poll.domain.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CandidateRepository extends JpaRepository<Candidate, Long> {

    List<Candidate> findByPollOrderByVoteCountDesc(Poll poll);
}