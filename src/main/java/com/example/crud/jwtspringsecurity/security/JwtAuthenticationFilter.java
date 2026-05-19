package com.example.crud.jwtspringsecurity.security;


import com.example.crud.jwtspringsecurity.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT Authentication Filter — runs once per HTTP request.
 *
 * Request flow:
 * ┌─────────────────────────────────────────────────────────────┐
 * │  HTTP Request                                               │
 * │       ↓                                                     │
 * │  JwtAuthFilter.doFilterInternal()                          │
 * │    1. Read "Authorization: Bearer <token>" header           │
 * │    2. Extract username from token                           │
 * │    3. Load UserDetails from database                        │
 * │    4. Validate token (signature + expiry + username)        │
 * │    5. Set Authentication in SecurityContext                 │
 * │       ↓                                                     │
 * │  Controller — SecurityContext has authenticated user        │
 * └─────────────────────────────────────────────────────────────┘
 *
 * If any step fails, the filter chain continues without authentication,
 * and Spring Security will return 401/403 for protected endpoints.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        // Skip filter for public endpoints to avoid unnecessary DB lookups
        final String requestPath = request.getServletPath();
        if (requestPath.startsWith("/api/auth/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Step 1: Read the Authorization header
        final String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // No JWT present — continue unauthenticated (security config will block if needed)
            filterChain.doFilter(request, response);
            return;
        }

        // Step 2: Extract the token (remove "Bearer " prefix)
        final String jwt = authHeader.substring(7);
        final String username;

        try {
            username = jwtService.extractUsername(jwt);
        } catch (Exception ex) {
            log.warn("Failed to extract username from JWT: {}", ex.getMessage());
            filterChain.doFilter(request, response);
            return;
        }

        // Step 3: If username is found and no auth is set in context yet
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Step 4: Load user from DB to get fresh roles and enabled status
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Step 5: Validate the token
            if (jwtService.isTokenValid(jwt, userDetails)) {

                // Step 6: Create an authentication token and store in SecurityContext
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null, // credentials not needed post-authentication
                                userDetails.getAuthorities()
                        );

                // Attach request details (IP, session info) to the auth token
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // Store authentication — downstream code can now call SecurityContextHolder.getContext().getAuthentication()
                SecurityContextHolder.getContext().setAuthentication(authToken);

                log.debug("Authenticated user: {} | Path: {}", username, requestPath);
            }
        }

        // Always continue the filter chain
        filterChain.doFilter(request, response);
    }
}
