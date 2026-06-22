package com.ceos23.spring_boot.poll.repository;

import com.ceos23.spring_boot.poll.domain.Poll;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PollRepository extends JpaRepository<Poll, Long> {
}