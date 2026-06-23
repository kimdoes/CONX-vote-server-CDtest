package com.ceos23.spring_boot.user.repository;

import com.ceos23.spring_boot.user.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    boolean existsByUserLogInId(String userLogInId);

    boolean existsByUserEmail(String userEmail);

    boolean existsByUsernameAndPassword(String username, String password);

    Optional<Member> findByUserLogInIdAndPassword(String userLogInId, String password);

    Optional<Member> findByUserLogInId(String userLogInId);
}
