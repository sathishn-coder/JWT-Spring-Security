package com.example.crud.jwtspringsecurity.config;


import com.example.crud.jwtspringsecurity.security.JwtAuthenticationEntryPoint;
import com.example.crud.jwtspringsecurity.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security Configuration for JWT-based stateless authentication.
 *
 * Key decisions:
 * ─────────────────────────────────────────────────────────────────
 * 1. STATELESS session — no HttpSession is created or used.
 *    All state lives in the JWT token itself.
 *
 * 2. CSRF disabled — safe for REST APIs because:
 *    - We don't use cookies for auth (JWT in Authorization header)
 *    - CSRF only protects session-based cookie auth
 *
 * 3. @EnableMethodSecurity — allows @PreAuthorize on controller methods,
 *    giving fine-grained per-method access control.
 *
 * 4. DaoAuthenticationProvider — wires together UserDetailsService
 *    (loads from DB) and PasswordEncoder (BCrypt comparison).
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)  // enables @PreAuthorize
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final JwtAuthenticationEntryPoint authEntryPoint;
    private final UserDetailsService userDetailsService;

    /**
     * Public endpoints — no token required.
     */
    private static final String[] PUBLIC_URLS = {
            "/api/auth/**",          // register + login
            "/swagger-ui/**",        // API docs (if you add Swagger later)
            "/v3/api-docs/**",
            "/actuator/health"
    };

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // ── Disable CSRF (not needed for stateless JWT) ──────────────────
                .csrf(AbstractHttpConfigurer::disable)

                // ── Custom 401 response for unauthenticated requests ─────────────
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(authEntryPoint)
                )

                // ── Stateless session — Spring Security won't create HttpSessions ─
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // ── URL-based authorization rules ─────────────────────────────────
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints — anyone can call
                        .requestMatchers(PUBLIC_URLS).permitAll()

                        // Product reads — authenticated users (USER or ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/products/**").hasAnyRole("USER", "ADMIN")

                        // Product writes — admin only
                        .requestMatchers(HttpMethod.POST, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/products/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/products/**").hasRole("ADMIN")

                        // Admin endpoints — admin only
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )

                // ── Wire in the custom auth provider ─────────────────────────────
                .authenticationProvider(authenticationProvider())

                // ── Add JWT filter BEFORE Spring's username/password filter ───────
                // This ensures the SecurityContext is populated from the JWT before
                // Spring's own filter runs
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Wires together:
     *  - UserDetailsService (loads user from MySQL)
     *  - PasswordEncoder (BCrypt comparison on login)
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * BCrypt password encoder.
     * strength=12 means 2^12 = 4096 bcrypt rounds — slower but more secure.
     * Default is 10; use 12 for user passwords.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    /**
     * Exposes the AuthenticationManager bean so AuthService can call authenticate().
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}