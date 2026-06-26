package com.ceos23.spring_boot.user.service;

import com.ceos23.spring_boot.user.domain.Member;
import com.ceos23.spring_boot.user.domain.Part;
import com.ceos23.spring_boot.user.domain.Team;
import com.ceos23.spring_boot.user.repository.MemberRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
@Profile("test")
public class MemberInitializer implements CommandLineRunner {
    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    public MemberInitializer(PasswordEncoder passwordEncoder, MemberRepository memberRepository) {
        this.passwordEncoder = passwordEncoder;
        this.memberRepository = memberRepository;
    }

    @Override
    public void run(String...args) {
        if (memberRepository.count() > 0) return; // 이미 있으면 스킵

        String password = passwordEncoder.encode("1q2w3e4r**");
        List<String> feCandidateNames = List.of("박유민", "권오진", "이윤서", "구민교", "이승연", "황영준",
                "남기림", "김민서", "김홍엽", "오유진");
        List<String> beCandidateNames = List.of("임종훈", "안준석", "황신애", "최우혁", "김동욱", "최승원",
                "오지송", "김태익", "김태희", "김도현");
        List<Team> teams = List.of(Team.Ditda, Team.JobDri, Team.Groupeat, Team.IPX, Team.CONX);
        List<Member> members = new ArrayList<>();

        for (int i = 0; i < feCandidateNames.size(); i++) {
            members.add(Member.create("fefefe"+i, password, "fe"+i+"@gmail.com",
                    Part.FRONTEND, feCandidateNames.get(i), teams.get(i/2)));
            members.add(Member.create("bebebe"+i, password, "be"+i+"@gmail.com",
                    Part.BACKEND, beCandidateNames.get(i), teams.get(i/2)));
        }

        memberRepository.saveAll(members);
    }
}