package com.ceos23.spring_boot.vote.controller;

import com.ceos23.spring_boot.auth.dto.LoginRequest;
import com.ceos23.spring_boot.auth.dto.SignupRequest;
import com.ceos23.spring_boot.poll.domain.Candidate;
import com.ceos23.spring_boot.poll.domain.Poll;
import com.ceos23.spring_boot.poll.repository.CandidateRepository;
import com.ceos23.spring_boot.poll.repository.PollRepository;
import com.ceos23.spring_boot.user.domain.Part;
import com.ceos23.spring_boot.user.domain.Team;
import com.ceos23.spring_boot.user.repository.MemberRepository;
import com.ceos23.spring_boot.vote.repository.VoteRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class VoteApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PollRepository pollRepository;

    @Autowired
    private CandidateRepository candidateRepository;

    @Autowired
    private VoteRepository voteRepository;

    @Autowired
    private MemberRepository memberRepository;

    @MockBean
    private RedisTemplate<Object, Object> redisTemplate;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() throws Exception {
        ValueOperations<Object, Object> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        voteRepository.deleteAll();
        candidateRepository.deleteAll();
        pollRepository.deleteAll();
        memberRepository.deleteAll();

        signup("ceos1234", "contact.conx@gmail.com", Part.BACKEND, "홍길동", Team.CONX);
    }

    @Test
    @DisplayName("투표를 만들 수 있다")
    void createPoll() throws Exception {
        String token = login();

        String request = """
                {
                  "title": "23기 데모데이 투표",
                  "voteType": "DEMO_DAY",
                  "candidates": [
                    {
                      "name": "Ditda",
                      "part": null,
                      "team": "DITDA"
                    },
                    {
                      "name": "JobDri",
                      "part": null,
                      "team": "JOBDRI"
                    },
                    {
                      "name": "Groupeat",
                      "part": null,
                      "team": "GROUPEAT"
                    }
                  ]
                }
                """;

        mockMvc.perform(post("/api/v1/polls")
                        .header("Authorization", token)
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
    @DisplayName("후보에게 처음 투표하면 득표 수가 증가한다")
    void vote() throws Exception {
        String token = login();
        Long pollId = createDemoDayPoll();

        Candidate ditda = findCandidateByName(pollId, "Ditda");

        String voteRequest = """
                {
                  "candidateId": %d
                }
                """.formatted(ditda.getId());

        mockMvc.perform(post("/api/v1/polls/{pollId}/votes", pollId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(voteRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("투표 성공"))
                .andExpect(jsonPath("$.payload.pollId").value(pollId))
                .andExpect(jsonPath("$.payload.candidateId").value(ditda.getId()))
                .andExpect(jsonPath("$.payload.candidateName").value("Ditda"));
    }

    @Test
    @DisplayName("이미 투표한 사용자가 다시 POST 투표하면 중복 투표 예외가 발생한다")
    void voteDuplicated() throws Exception {
        String token = login();
        Long pollId = createDemoDayPoll();

        Candidate ditda = findCandidateByName(pollId, "Ditda");
        Candidate jobDri = findCandidateByName(pollId, "JobDri");

        requestVote(pollId, ditda.getId(), token)
                .andExpect(status().isOk());

        String voteRequest = """
                {
                  "candidateId": %d
                }
                """.formatted(jobDri.getId());

        mockMvc.perform(post("/api/v1/polls/{pollId}/votes", pollId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(voteRequest))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.code").value("E010"))
                .andExpect(jsonPath("$.message").value("이미 투표를 완료했습니다."));
    }

    @Test
    @DisplayName("내 투표 여부를 조회할 수 있다")
    void getMyVoteStatus() throws Exception {
        String token = login();
        Long pollId = createDemoDayPoll();

        mockMvc.perform(get("/api/v1/polls/{pollId}/votes/me", pollId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("내 투표 여부 조회 성공"))
                .andExpect(jsonPath("$.payload.hasVoted").value(false))
                .andExpect(jsonPath("$.payload.candidateId").doesNotExist())
                .andExpect(jsonPath("$.payload.candidateName").doesNotExist());

        Candidate ditda = findCandidateByName(pollId, "Ditda");

        requestVote(pollId, ditda.getId(), token)
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/polls/{pollId}/votes/me", pollId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("내 투표 여부 조회 성공"))
                .andExpect(jsonPath("$.payload.hasVoted").value(true))
                .andExpect(jsonPath("$.payload.candidateId").value(ditda.getId()))
                .andExpect(jsonPath("$.payload.candidateName").value("Ditda"));
    }

    @Test
    @DisplayName("재투표하면 기존 후보 득표 수가 감소하고 새 후보 득표 수가 증가한다")
    void revote() throws Exception {
        String token = login();
        Long pollId = createDemoDayPoll();

        Candidate ditda = findCandidateByName(pollId, "Ditda");
        Candidate jobDri = findCandidateByName(pollId, "JobDri");

        requestVote(pollId, ditda.getId(), token)
                .andExpect(status().isOk());

        String revoteRequest = """
                {
                  "candidateId": %d
                }
                """.formatted(jobDri.getId());

        mockMvc.perform(patch("/api/v1/polls/{pollId}/votes", pollId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(revoteRequest))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("재투표 성공"))
                .andExpect(jsonPath("$.payload.pollId").value(pollId))
                .andExpect(jsonPath("$.payload.candidateId").value(jobDri.getId()))
                .andExpect(jsonPath("$.payload.candidateName").value("JobDri"));

        mockMvc.perform(get("/api/v1/polls/{pollId}/votes/me", pollId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.hasVoted").value(true))
                .andExpect(jsonPath("$.payload.candidateId").value(jobDri.getId()))
                .andExpect(jsonPath("$.payload.candidateName").value("JobDri"));
    }

    @Test
    @DisplayName("투표를 취소할 수 있다")
    void cancelVote() throws Exception {
        String token = login();
        Long pollId = createDemoDayPoll();

        Candidate ditda = findCandidateByName(pollId, "Ditda");

        requestVote(pollId, ditda.getId(), token)
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/v1/polls/{pollId}/votes", pollId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("투표 취소 성공"));

        mockMvc.perform(get("/api/v1/polls/{pollId}/votes/me", pollId)
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.payload.hasVoted").value(false))
                .andExpect(jsonPath("$.payload.candidateId").doesNotExist())
                .andExpect(jsonPath("$.payload.candidateName").doesNotExist());
    }

    @Test
    @DisplayName("투표 결과는 득표 수 내림차순으로 조회된다")
    void getPollResultSortedByVoteCountDesc() throws Exception {
        Long pollId = createDemoDayPoll();

        signup("user2", "user2@gmail.com", Part.BACKEND, "유저2", Team.CONX);
        signup("user3", "user3@gmail.com", Part.BACKEND, "유저3", Team.IPX);
        signup("user4", "user4@gmail.com", Part.BACKEND, "유저4", Team.CONX);
        signup("user5", "user5@gmail.com", Part.BACKEND, "유저5", Team.IPX);
        signup("user6", "user6@gmail.com", Part.BACKEND, "유저6", Team.CONX);

        String token1 = login("ceos1234", "ceos1234**");
        String token2 = login("user2", "ceos1234**");
        String token3 = login("user3", "ceos1234**");
        String token4 = login("user4", "ceos1234**");
        String token5 = login("user5", "ceos1234**");
        String token6 = login("user6", "ceos1234**");

        Candidate ditda = findCandidateByName(pollId, "Ditda");
        Candidate jobDri = findCandidateByName(pollId, "JobDri");
        Candidate groupeat = findCandidateByName(pollId, "Groupeat");

        requestVote(pollId, ditda.getId(), token1)
                .andExpect(status().isOk());

        requestVote(pollId, jobDri.getId(), token2)
                .andExpect(status().isOk());
        requestVote(pollId, jobDri.getId(), token3)
                .andExpect(status().isOk());

        requestVote(pollId, groupeat.getId(), token4)
                .andExpect(status().isOk());
        requestVote(pollId, groupeat.getId(), token5)
                .andExpect(status().isOk());
        requestVote(pollId, groupeat.getId(), token6)
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/v1/polls/{pollId}/results", pollId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("투표 결과 조회 성공"))
                .andExpect(jsonPath("$.payload.pollId").value(pollId))
                .andExpect(jsonPath("$.payload.title").value("23기 데모데이 투표"))
                .andExpect(jsonPath("$.payload.voteType").value("DEMO_DAY"))
                .andExpect(jsonPath("$.payload.results[0].name").value("Groupeat"))
                .andExpect(jsonPath("$.payload.results[0].voteCount").value(3))
                .andExpect(jsonPath("$.payload.results[1].name").value("JobDri"))
                .andExpect(jsonPath("$.payload.results[1].voteCount").value(2))
                .andExpect(jsonPath("$.payload.results[2].name").value("Ditda"))
                .andExpect(jsonPath("$.payload.results[2].voteCount").value(1));
    }

    @Test
    @DisplayName("존재하지 않는 투표 결과를 조회하면 예외가 발생한다")
    void getPollResultWithInvalidPollId() throws Exception {
        mockMvc.perform(get("/api/v1/polls/{pollId}/results", 999L))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.code").value("E005"))
                .andExpect(jsonPath("$.message").value("투표를 찾을 수 없습니다."));
    }

    @Test
    @DisplayName("해당 투표에 속하지 않은 후보에게 투표하면 예외가 발생한다")
    void voteCandidateNotInPoll() throws Exception {
        String token = login();

        Long pollId = createDemoDayPoll();
        Long anotherPollId = createPartLeaderPoll();

        Candidate backendCandidate = findCandidateByName(anotherPollId, "백엔드 후보 A");

        String voteRequest = """
                {
                  "candidateId": %d
                }
                """.formatted(backendCandidate.getId());

        mockMvc.perform(post("/api/v1/polls/{pollId}/votes", pollId)
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(voteRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.code").value("E007"))
                .andExpect(jsonPath("$.message").value("해당 투표에 속한 후보가 아닙니다."));
    }

    @Test
    @DisplayName("투표 목록을 조회할 수 있다")
    void getPolls() throws Exception {
        String token = login();
        Long pollId = createDemoDayPoll();

        mockMvc.perform(get("/api/v1/polls")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("투표 목록 조회 성공"))
                .andExpect(jsonPath("$.payload[0].pollId").value(pollId))
                .andExpect(jsonPath("$.payload[0].title").value("23기 데모데이 투표"))
                .andExpect(jsonPath("$.payload[0].voteType").value("DEMO_DAY"));
    }

    private Long createDemoDayPoll() throws Exception {
        String token = login();

        String request = """
                {
                  "title": "23기 데모데이 투표",
                  "voteType": "DEMO_DAY",
                  "candidates": [
                    {
                      "name": "Ditda",
                      "part": null,
                      "team": "DITDA"
                    },
                    {
                      "name": "JobDri",
                      "part": null,
                      "team": "JOBDRI"
                    },
                    {
                      "name": "Groupeat",
                      "part": null,
                      "team": "GROUPEAT"
                    }
                  ]
                }
                """;

        String response = mockMvc.perform(post("/api/v1/polls")
                        .header("Authorization", token)
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
        String token = login();

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
                        .header("Authorization", token)
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

    private org.springframework.test.web.servlet.ResultActions requestVote(
            Long pollId,
            Long candidateId,
            String token
    ) throws Exception {
        String request = """
                {
                  "candidateId": %d
                }
                """.formatted(candidateId);

        return mockMvc.perform(post("/api/v1/polls/{pollId}/votes", pollId)
                .header("Authorization", token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(request));
    }

    private void signup(String userId, String email, Part part, String username, Team team) throws Exception {
        SignupRequest signupRequest = new SignupRequest(
                userId,
                "ceos1234**",
                email,
                part,
                username,
                team
        );

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk());
    }

    private String login() throws Exception {
        return login("ceos1234", "ceos1234**");
    }

    private String login(String userId, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(userId, password);

        String responseBody = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        JsonNode jsonNode = objectMapper.readTree(responseBody);
        String accessToken = jsonNode.path("payload").path("accessToken").asText();

        if (accessToken == null || accessToken.isBlank()) {
            throw new IllegalArgumentException("accessToken이 없습니다. responseBody = " + responseBody);
        }

        if (accessToken.startsWith("Bearer ")) {
            return accessToken;
        }

        return "Bearer " + accessToken;
    }
}