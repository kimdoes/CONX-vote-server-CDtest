package com.ceos23.spring_boot.global.security.filter;

import com.ceos23.spring_boot.global.exception.CustomAuthenticationException;
import com.ceos23.spring_boot.global.exception.CustomException;
import com.ceos23.spring_boot.global.security.token.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JWTAuthenticationFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;

    public JWTAuthenticationFilter(TokenProvider tokenProvider){
        this.tokenProvider = tokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {

            String accessToken = tokenProvider.getTokenFromHeader(req);

            if (!StringUtils.hasText(accessToken)) {
                filterChain.doFilter(req, res);
                return;
            }

            Authentication authentication = tokenProvider.getAuthentication(accessToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            filterChain.doFilter(req, res);

        } catch (CustomException ce){
            ce.printStackTrace();
            throw new CustomAuthenticationException(ce);
        }
    }
}
