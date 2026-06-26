package com.ceos23.spring_boot.user.service;

import com.ceos23.spring_boot.user.domain.Member;
import com.ceos23.spring_boot.user.domain.Part;
import com.ceos23.spring_boot.user.dto.MemberGroupResponse;
import com.ceos23.spring_boot.user.dto.MemberResponse;
import com.ceos23.spring_boot.user.repository.MemberRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional(readOnly = true)
    public MemberGroupResponse getMembers() {
        List<Member> members = memberRepository.findAllByOrderByPartAscUsernameAsc();

        List<MemberResponse> frontend = members.stream()
                .filter(member -> member.getPart() == Part.FRONTEND)
                .map(MemberResponse::from)
                .toList();

        List<MemberResponse> backend = members.stream()
                .filter(member -> member.getPart() == Part.BACKEND)
                .map(MemberResponse::from)
                .toList();

        return MemberGroupResponse.of(frontend, backend);
    }
}