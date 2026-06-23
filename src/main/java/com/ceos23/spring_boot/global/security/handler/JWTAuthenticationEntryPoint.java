package com.ceos23.spring_boot.global.security.handler;

import com.ceos23.spring_boot.global.exception.CustomAuthenticationException;
import com.ceos23.spring_boot.global.exception.ErrorCode;
import com.ceos23.spring_boot.global.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

public class JWTAuthenticationEntryPoint implements AuthenticationEntryPoint {
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(@NonNull HttpServletRequest request,
                         @NonNull HttpServletResponse response,
                         @NonNull AuthenticationException ae) throws IOException, ServletException {
        System.out.println(ae.getClass().getName());
        if (ae instanceof CustomAuthenticationException cae){
            ErrorResponseDTO errorResponseDTO = ErrorResponseDTO.create(cae.getCode());

            String responseBody = objectMapper.writeValueAsString(errorResponseDTO);

            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.setStatus(cae.getCode().getHttpStatus().value());
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(responseBody);
        } else {
            response.setStatus(HttpStatus.FORBIDDEN.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(
                    objectMapper.writeValueAsString(
                            ApiResponse.fail(ErrorCode.FORBIDDEN)
                    )
            );
        }
    }
}
