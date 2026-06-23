package com.ceos23.spring_boot.auth.service;

import com.ceos23.spring_boot.auth.dto.LoginRequest;
import com.ceos23.spring_boot.auth.dto.LoginResponse;
import com.ceos23.spring_boot.auth.dto.SignupRequest;
import com.ceos23.spring_boot.auth.dto.SignupResponse;
import com.ceos23.spring_boot.global.exception.CustomException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import com.ceos23.spring_boot.global.security.token.JWTType;
import com.ceos23.spring_boot.global.security.token.TokenProvider;
import com.ceos23.spring_boot.global.security.userDetails.CustomUserDetails;
import com.ceos23.spring_boot.user.domain.Member;
import com.ceos23.spring_boot.user.repository.MemberRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder encoder;
    private final TokenProvider tokenProvider;
    private final RedisTemplate<Object, Object> redisTemplate;

    public SignupResponse signup(SignupRequest request) {
        if (memberRepository.existsByUserLogInId(request.userId())){
            throw new CustomException(ErrorCode.DUPLICATE_USERID);
        }

        if (memberRepository.existsByUserEmail(request.email())){
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }

        String encodedPassword = encoder.encode(request.password());
        Member member = Member.create(request, encodedPassword);
        Member savedMember = memberRepository.save(member);

        return SignupResponse.of(savedMember.getId(), savedMember.getUsername());
    }


    public LoginResponse login(LoginRequest request) {
        String userLoginId = request.userLoginId();
        String password = request.password();

        Member member = memberRepository.findByUserLogInId(userLoginId).orElseThrow(
                () -> new CustomException(ErrorCode.UNAUTHORIZED)
        );

        if (!encoder.matches(password, member.getPassword())){
            throw new CustomException(ErrorCode.UNAUTHORIZED);
        }

        CustomUserDetails userDetails = CustomUserDetails.of(member);
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );

        String accessToken = tokenProvider.createToken(member.getId(), authentication, JWTType.ACCESS);
        String refreshToken = tokenProvider.createToken(member.getId(), authentication, JWTType.REFRESH);

        redisTemplate.opsForValue().set(
                "refreshToken:" + member.getId(),
                refreshToken,
                JWTType.REFRESH.getValidTime(),
                TimeUnit.SECONDS
        );

        return LoginResponse.of(accessToken, refreshToken);
    }
}