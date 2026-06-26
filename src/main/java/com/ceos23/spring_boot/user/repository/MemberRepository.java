package com.ceos23.spring_boot.user.repository;

import com.ceos23.spring_boot.user.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByUserLogInId(String userLogInId);

    boolean existsByUserEmail(String userEmail);

    Optional<Member> findByUserLogInId(String userLogInId);

    List<Member> findAllByOrderByPartAscUsernameAsc();
}