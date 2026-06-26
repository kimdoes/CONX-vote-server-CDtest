package com.ceos23.spring_boot.auth.dto;

import com.ceos23.spring_boot.user.domain.Member;
import com.ceos23.spring_boot.user.domain.Part;
import com.ceos23.spring_boot.user.domain.Team;

public record LoginResponseDTO(
        long userId,
        String username,
        Part part,
        Team team
) {
    public static LoginResponseDTO create(Member member){
        return new LoginResponseDTO(
                member.getId(), member.getUsername(), member.getPart(), member.getTeam()
        );
    }
}
