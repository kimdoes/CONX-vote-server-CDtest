package com.ceos23.spring_boot.user.controller;

import com.ceos23.spring_boot.auth.dto.LoginRequest;
import com.ceos23.spring_boot.auth.dto.SignupRequest;
import com.ceos23.spring_boot.user.domain.Part;
import com.ceos23.spring_boot.user.domain.Team;
import com.ceos23.spring_boot.user.repository.MemberRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

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
                .andExpect(jsonPath("$.payload.frontend[0].username").value("프론트멤버"))
                .andExpect(jsonPath("$.payload.frontend[0].part").value("FRONTEND"))
                .andExpect(jsonPath("$.payload.frontend[0].team").value("IPX"))
                .andExpect(jsonPath("$.payload.backend[0].username").value("백엔드멤버"))
                .andExpect(jsonPath("$.payload.backend[0].part").value("BACKEND"))
                .andExpect(jsonPath("$.payload.backend[0].team").value("CONX"));
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

        return mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andReturn()
                .getRequest().getHeader("Authorization");
    }
}