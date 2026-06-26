package com.ceos23.spring_boot.global.config;

import com.ceos23.spring_boot.global.security.filter.JWTAuthenticationFilter;
import com.ceos23.spring_boot.global.security.handler.CustomAccessDeniedHandler;
import com.ceos23.spring_boot.global.security.handler.JWTAuthenticationEntryPoint;
import com.ceos23.spring_boot.global.security.token.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    @Bean
    public JWTAuthenticationFilter jwtAuthenticationFilter(TokenProvider tokenProvider) {
        return new JWTAuthenticationFilter(tokenProvider);
    }

    @Bean
    public JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JWTAuthenticationEntryPoint();
    }

    @Bean
    public CustomAccessDeniedHandler customAccessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JWTAuthenticationFilter jwtAuthenticationFilter,
            CustomAccessDeniedHandler customAccessDeniedHandler,
            JWTAuthenticationEntryPoint jwtAuthenticationEntryPoint,
            CorsConfigurationSource corsConfigurationSource) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/swagger-ui/index.html",
                                "/v3/api-docs/**",
                                "/h2-console/**",
                                "/",
                                "/css/**",
                                "/images/**",
                                "/favicon.ico/**",
                                "/api/v1/auth/**"
                        ).permitAll()

                        //헬스체크
                        .requestMatchers(HttpMethod.GET,
                                "/health/**"
                        ).permitAll()

                        //투표 결과보기
                        .requestMatchers(
                                "/api/v1/polls/*/results"
                        ).permitAll()

                        .anyRequest().authenticated()
                )

                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                )

                .exceptionHandling(e -> e
                        .accessDeniedHandler(customAccessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("https://www.conx.co.kr"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}