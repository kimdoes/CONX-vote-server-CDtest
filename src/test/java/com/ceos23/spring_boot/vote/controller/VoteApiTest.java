package com.ceos23.spring_boot.vote.controller;

import com.ceos23.spring_boot.poll.domain.Candidate;
import com.ceos23.spring_boot.poll.domain.Poll;
import com.ceos23.spring_boot.poll.repository.CandidateRepository;
import com.ceos23.spring_boot.poll.repository.PollRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class VoteApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @BeforeEach
    void setUp() {
        candidateRepository.deleteAll();
        pollRepository.deleteAll();
    }

    @Test
    @DisplayName("투표를 만들 수 있다")
    void createPoll() throws Exception {
        String request = """
                {
                  "title": "23기 데모데이 투표",
                  "voteType": "DEMO_DAY",
                  "candidates": [
                    {
                      "name": "DiggIndie",
                      "part": null,
                      "team": "DIGG_INDIE"
                    },
                    {
                      "name": "모델리",
                      "part": null,
                      "team": "MODELLI"
                    },
                    {
                      "name": "캐치업",
                      "part": null,
                      "team": "CATCH_UP"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/v1/polls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value("투표 생성 성공"))
                .andExpect(jsonPath("$.payload.pollId").exists())
                .andExpect(jsonPath("$.payload.title").value("23기 데모데이 투표"))
                .andExpect(jsonPath("$.payload.voteType").value("DEMO_DAY"));
    }

    @Test
    @DisplayName("후보에게 투표하면 득표 수가 증가한다")
    void vote() throws Exception {
        Long pollId = createDemoDayPoll();

        Candidate diggIndie = findCandidateByName(pollId, "DiggIndie");

        String voteRequest = """
                {
                  "candidateId": %d
                }
                """.formatted(diggIndie.getId());

        mockMvc.perform(post("/api/v1/polls/{pollId}/votes", pollId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(voteRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("투표 성공"))
                .andExpect(jsonPath("$.payload.pollId").value(pollId))
                .andExpect(jsonPath("$.payload.candidateId").value(diggIndie.getId()));
    }

    @Test
    @DisplayName("투표 결과는 득표 수 내림차순으로 조회된다")
    void getPollResultSortedByVoteCountDesc() throws Exception {
        Long pollId = createDemoDayPoll();

        Candidate diggIndie = findCandidateByName(pollId, "DiggIndie");
        Candidate modeli = findCandidateByName(pollId, "모델리");
        Candidate catchUp = findCandidateByName(pollId, "캐치업");

        vote(pollId, diggIndie.getId());
        vote(pollId, modeli.getId());
        vote(pollId, modeli.getId());
        vote(pollId, catchUp.getId());
        vote(pollId, catchUp.getId());
        vote(pollId, catchUp.getId());

        mockMvc.perform(get("/api/v1/polls/{pollId}/results", pollId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("투표 결과 조회 성공"))
                .andExpect(jsonPath("$.payload.pollId").value(pollId))
                .andExpect(jsonPath("$.payload.title").value("23기 데모데이 투표"))
                .andExpect(jsonPath("$.payload.voteType").value("DEMO_DAY"))
                .andExpect(jsonPath("$.payload.results[0].name").value("캐치업"))
                .andExpect(jsonPath("$.payload.results[0].voteCount").value(3))
                .andExpect(jsonPath("$.payload.results[1].name").value("모델리"))
                .andExpect(jsonPath("$.payload.results[1].voteCount").value(2))
                .andExpect(jsonPath("$.payload.results[2].name").value("DiggIndie"))
                .andExpect(jsonPath("$.payload.results[2].voteCount").value(1));
    }

    @Test
    @DisplayName("존재하지 않는 투표 결과를 조회하면 예외가 발생한다")
    void getPollResultWithInvalidPollId() throws Exception {
        mockMvc.perform(get("/api/v1/polls/{pollId}/results", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("POLL_NOT_FOUND"))
                .andExpect(jsonPath("$.message").value("투표를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("해당 투표에 속하지 않은 후보에게 투표하면 예외가 발생한다")
    void voteCandidateNotInPoll() throws Exception {
        Long pollId = createDemoDayPoll();
        Long anotherPollId = createPartLeaderPoll();

        Candidate backendCandidate = findCandidateByName(anotherPollId, "백엔드 후보 A");

        String voteRequest = """
                {
                  "candidateId": %d
                }
                """.formatted(backendCandidate.getId());

        mockMvc.perform(post("/api/v1/polls/{pollId}/votes", pollId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(voteRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("CANDIDATE_NOT_IN_POLL"))
                .andExpect(jsonPath("$.message").value("해당 투표에 속한 후보가 아닙니다."));
    }

    private Long createDemoDayPoll() throws Exception {
        String request = """
                {
                  "title": "23기 데모데이 투표",
                  "voteType": "DEMO_DAY",
                  "candidates": [
                    {
                      "name": "DiggIndie",
                      "part": null,
                      "team": "DIGG_INDIE"
                    },
                    {
                      "name": "모델리",
                      "part": null,
                      "team": "MODELLI"
                    },
                    {
                      "name": "캐치업",
                      "part": null,
                      "team": "CATCH_UP"
                    }
                  ]
                }
                """;

        String response = mockMvc.perform(post("/api/v1/polls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(response);

        return jsonNode.path("payload").path("pollId").asLong();
    }

    private Long createPartLeaderPoll() throws Exception {
        String request = """
                {
                  "title": "23기 백엔드 파트장 투표",
                  "voteType": "PART_LEADER",
                  "candidates": [
                    {
                      "name": "백엔드 후보 A",
                      "part": "BACKEND",
                      "team": null
                    },
                    {
                      "name": "백엔드 후보 B",
                      "part": "BACKEND",
                      "team": null
                    }
                  ]
                }
                """;

        String response = mockMvc.perform(post("/api/v1/polls")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(response);

        return jsonNode.path("payload").path("pollId").asLong();
    }

    private Candidate findCandidateByName(Long pollId, String name) {
        Poll poll = pollRepository.findById(pollId)
                .orElseThrow();

        List<Candidate> candidates = candidateRepository.findByPollOrderByVoteCountDesc(poll);

        return candidates.stream()
                .filter(candidate -> candidate.getName().equals(name))
                .findFirst()
                .orElseThrow();
    }

    private void vote(Long pollId, Long candidateId) throws Exception {
        String request = """
                {
                  "candidateId": %d
                }
                """.formatted(candidateId);

        mockMvc.perform(post("/api/v1/polls/{pollId}/votes", pollId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(request))
                .andExpect(status().isOk());
    }
}