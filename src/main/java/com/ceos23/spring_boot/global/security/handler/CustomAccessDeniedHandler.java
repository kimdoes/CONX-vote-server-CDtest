package com.ceos23.spring_boot.global.security.handler;

import com.ceos23.spring_boot.global.exception.ErrorCode;
import com.ceos23.spring_boot.global.response.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest req,
                       HttpServletResponse res,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        res.setStatus(HttpStatus.FORBIDDEN.value());
        res.setContentType("application/json;charset=UTF-8");
        res.getWriter().write(
                objectMapper.writeValueAsString(
                        ApiResponse.fail(ErrorCode.FORBIDDEN)
                )
        );
    }
}
