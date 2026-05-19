package com.example.crud.jwtspringsecurity.security;



import com.example.crud.jwtspringsecurity.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Custom entry point for unauthenticated requests.
 *
 * Without this, Spring Security returns an HTML redirect to /login for 401 errors.
 * This component returns a clean JSON response instead, which is what REST clients expect.
 *
 * Triggered when:
 *  - No JWT token is provided
 *  - JWT token is expired or malformed
 *  - User account is disabled
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException authException
    ) throws IOException {

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse<Void> errorResponse = ApiResponse.error(
                "Authentication required. Please provide a valid JWT token."
        );

        objectMapper.writeValue(response.getOutputStream(), errorResponse);
    }
}
