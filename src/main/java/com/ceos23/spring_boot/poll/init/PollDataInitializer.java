package com.ceos23.spring_boot.poll.init;

import com.ceos23.spring_boot.global.type.Part;
import com.ceos23.spring_boot.global.type.Team;
import com.ceos23.spring_boot.global.type.VoteType;
import com.ceos23.spring_boot.poll.domain.Candidate;
import com.ceos23.spring_boot.poll.domain.Poll;
import com.ceos23.spring_boot.poll.repository.CandidateRepository;
import com.ceos23.spring_boot.poll.repository.PollRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class PollDataInitializer implements CommandLineRunner {

    private final PollRepository pollRepository;
    private final CandidateRepository candidateRepository;

    public PollDataInitializer(PollRepository pollRepository, CandidateRepository candidateRepository) {
        this.pollRepository = pollRepository;
        this.candidateRepository = candidateRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        createPartLeaderPoll(
                "FE 파트장 투표",
                Part.FRONTEND,
                List.of(
                        "박유민",
                        "권오진",
                        "이윤서",
                        "구민교",
                        "이승연",
                        "황영준",
                        "남기림",
                        "김민서",
                        "김홍엽",
                        "오유진"
                )
        );

        createPartLeaderPoll(
                "BE 파트장 투표",
                Part.BACKEND,
                List.of(
                        "임종훈",
                        "안준석",
                        "황신애",
                        "최우혁",
                        "김동욱",
                        "최승원",
                        "오지송",
                        "김태익",
                        "김태희",
                        "김도현"
                )
        );

        createDemoDayPoll(
                "데모데이 투표",
                List.of(
                        CandidateSeed.of("Ditda", Team.DITDA),
                        CandidateSeed.of("JobDri", Team.JOBDRI),
                        CandidateSeed.of("Groupeat", Team.GROUPEAT),
                        CandidateSeed.of("IPX", Team.IPX),
                        CandidateSeed.of("CONX", Team.CONX)
                )
        );
    }

    private void createPartLeaderPoll(String title, Part part, List<String> candidateNames) {
        if (pollRepository.existsByTitle(title)) {
            return;
        }

        Poll poll = pollRepository.save(Poll.of(title, VoteType.PART_LEADER));

        List<Candidate> candidates = candidateNames.stream()
                .map(name -> Candidate.of(poll, name, part, null))
                .toList();

        candidateRepository.saveAll(candidates);
    }

    private void createDemoDayPoll(String title, List<CandidateSeed> candidateSeeds) {
        if (pollRepository.existsByTitle(title)) {
            return;
        }

        Poll poll = pollRepository.save(Poll.of(title, VoteType.DEMO_DAY));

        List<Candidate> candidates = candidateSeeds.stream()
                .map(candidateSeed -> Candidate.of(
                        poll,
                        candidateSeed.name(),
                        null,
                        candidateSeed.team()
                ))
                .toList();

        candidateRepository.saveAll(candidates);
    }

    private record CandidateSeed(String name, Team team) {

        private static CandidateSeed of(String name, Team team) {
            return new CandidateSeed(name, team);
        }
    }
}