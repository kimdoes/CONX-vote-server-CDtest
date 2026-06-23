package com.ceos23.spring_boot.auth.controller;

import com.ceos23.spring_boot.auth.dto.LoginRequest;
import com.ceos23.spring_boot.auth.dto.SignupRequest;
import com.ceos23.spring_boot.auth.dto.SignupResponse;
import com.ceos23.spring_boot.global.response.ApiResponse;
import com.ceos23.spring_boot.user.domain.Member;
import com.ceos23.spring_boot.user.domain.Part;
import com.ceos23.spring_boot.user.domain.Team;
import com.ceos23.spring_boot.user.repository.MemberRepository;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MemberRepository memberRepository;

    @Test
    @Transactional
    @DisplayName("회원가입")
    void signup() throws Exception{
        SignupRequest signupRequest = new SignupRequest(
                "ceos1234", "ceos1234**", "contact.conx@gmail.com", Part.BACKEND, "홍길동", Team.CONX
        );

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andReturn();

        ApiResponse<SignupResponse> response = objectMapper.readValue(
                mvcResult.getResponse().getContentAsString(),
                new TypeReference<ApiResponse<SignupResponse>>() {}
        );

        SignupResponse res = response.getPayload();

        Member member = memberRepository.findByUserLogInId("ceos1234").get();

        assertThat(member.getUserEmail()).isEqualTo("contact.conx@gmail.com");
        assertThat(member.getTeam()).isEqualTo(Team.CONX);
    }

    @Test
    @Transactional
    @DisplayName("중복된 아이디로 회원가입")
    void signupWithDuplicatedId() throws Exception{
        SignupRequest signupRequest = new SignupRequest(
                "ceos1234", "ceos1234**", "contact.conx@gmail.com", Part.BACKEND, "홍길동", Team.CONX
        );

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andReturn();

        SignupRequest signupRequest2 = new SignupRequest(
                "ceos1234", "ceos1234**", "nan@gmail.com", Part.BACKEND, "홍길동", Team.CONX
        );

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code")
                        .value("E001"));
    }

    @Test
    @Transactional
    @DisplayName("중복된 이메일로 회원가입")
    void signupWithDuplicatedEmail() throws Exception{
        SignupRequest signupRequest = new SignupRequest(
                "ceos1234", "ceos1234**", "contact.conx@gmail.com", Part.BACKEND, "홍길동", Team.CONX
        );

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andReturn();

        SignupRequest signupRequest2 = new SignupRequest(
                "ceos4321", "ceos1234**", "contact.conx@gmail.com", Part.BACKEND, "홍길동", Team.CONX
        );

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest2)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.code")
                        .value("E002"));
    }

    @Test
    @Transactional
    @DisplayName("로그인")
    void login() throws Exception {
        SignupRequest signupRequest = new SignupRequest(
                "ceos1234", "ceos1234**", "contact.conx@gmail.com", Part.BACKEND, "홍길동", Team.CONX
        );

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andReturn();

        LoginRequest loginRequest = new LoginRequest("ceos1234", "ceos1234**");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk());
    }

    @Test
    @Transactional
    @DisplayName("로그인 시 아이디 다름")
    void loginWithIdError() throws Exception {
        SignupRequest signupRequest = new SignupRequest(
                "ceos1234", "ceos1234**", "contact.conx@gmail.com", Part.BACKEND, "홍길동", Team.CONX
        );

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andReturn();

        LoginRequest loginRequest = new LoginRequest("ceos1237", "ceos1234**");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code")
                        .value("E004"));
    }

    @Test
    @Transactional
    @DisplayName("로그인 시 비밀번호 다름")
    void loginWithPasswordError() throws Exception {
        SignupRequest signupRequest = new SignupRequest(
                "ceos1234", "ceos1234**", "contact.conx@gmail.com", Part.BACKEND, "홍길동", Team.CONX
        );

        mockMvc.perform(post("/api/v1/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(signupRequest)))
                .andExpect(status().isOk())
                .andReturn();

        LoginRequest loginRequest = new LoginRequest("ceos1234", "ceos1237**");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.code")
                        .value("E004"));
    }
}