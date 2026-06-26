package com.ceos23.spring_boot.user.controller;

import com.ceos23.spring_boot.auth.dto.LoginRequest;
import com.ceos23.spring_boot.auth.dto.SignupRequest;
import com.ceos23.spring_boot.user.domain.Part;
import com.ceos23.spring_boot.user.domain.Team;
import com.ceos23.spring_boot.user.repository.MemberRepository;
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
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("ci")
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @MockBean
    private RedisTemplate<Object, Object> redisTemplate;

    @SuppressWarnings("unchecked")
    @BeforeEach
    void setUp() {
        ValueOperations<Object, Object> valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);

        memberRepository.deleteAll();
    }

    @Test
    @Transactional
    @DisplayName("멤버 목록을 파트별로 조회할 수 있다")
    void getMembers() throws Exception {
        signup("frontend1234", "frontend@gmail.com", Part.FRONTEND, "프론트멤버", Team.IPX);
        signup("backend1234", "backend@gmail.com", Part.BACKEND, "백엔드멤버", Team.CONX);

        String token = login("backend1234", "ceos1234**");

        mockMvc.perform(get("/api/v1/members")
                        .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("멤버 목록 조회 성공"))

                .andExpect(jsonPath("$.payload.frontend[*].username", hasItem("프론트멤버")))
                .andExpect(jsonPath("$.payload.frontend[*].part", hasItem("FRONTEND")))
                .andExpect(jsonPath("$.payload.frontend[*].team", hasItem("IPX")))

                .andExpect(jsonPath("$.payload.backend[*].username", hasItem("백엔드멤버")))
                .andExpect(jsonPath("$.payload.backend[*].part", hasItem("BACKEND")))
                .andExpect(jsonPath("$.payload.backend[*].team", hasItem("CONX")));
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

    private String login(String userId, String password) throws Exception {
        LoginRequest loginRequest = new LoginRequest(userId, password);

        String token = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getHeader("Authorization");

        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Authorization 헤더가 없습니다.");
        }

        return token;
    }
}