package com.example.crud.jwtspringsecurity.service;





import com.example.crud.jwtspringsecurity.dto.AuthResponse;
import com.example.crud.jwtspringsecurity.dto.LoginRequest;
import com.example.crud.jwtspringsecurity.dto.RegisterRequest;
import com.example.crud.jwtspringsecurity.entity.Role;
import com.example.crud.jwtspringsecurity.entity.User;
import com.example.crud.jwtspringsecurity.exception.InvalidCredentialsException;
import com.example.crud.jwtspringsecurity.exception.UserAlreadyExistsException;
import com.example.crud.jwtspringsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

/**
 * Authentication Service — handles registration and login.
 *
 * Registration flow:
 *  1. Validate username/email uniqueness
 *  2. Encode password with BCrypt
 *  3. Save user to DB with ROLE_USER
 *  4. Generate JWT
 *  5. Return token + user info
 *
 * Login flow:
 *  1. Delegate to AuthenticationManager (loads user, compares BCrypt hash)
 *  2. Generate JWT with role claim
 *  3. Return token + user info
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    // ─── Registration ─────────────────────────────────────────────────────────

    @Transactional
    public AuthResponse register(RegisterRequest request) {

        // Guard: username uniqueness
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new UserAlreadyExistsException(
                    "Username '" + request.getUsername() + "' is already taken");
        }

        // Guard: email uniqueness
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new UserAlreadyExistsException(
                    "Email '" + request.getEmail() + "' is already registered");
        }

        // Build the user entity
        User user = User.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // BCrypt hash
                .role(Role.ROLE_USER) // new users start as ROLE_USER
                .isActive(true)
                .build();

        User savedUser = userRepository.save(user);
        log.info("New user registered: {} ({})", savedUser.getUsername(), savedUser.getEmail());

        // Generate JWT with role embedded
        String token = jwtService.generateToken(buildClaims(savedUser), savedUser);

        return buildAuthResponse(token, savedUser, "Registration successful! Welcome aboard.");
    }

    // ─── Login ────────────────────────────────────────────────────────────────

    public AuthResponse login(LoginRequest request) {
        try {
            // This triggers DaoAuthenticationProvider:
            //  1. loadUserByUsername(request.getUsername())
            //  2. BCrypt.matches(request.getPassword(), user.getPassword())
            //  Throws BadCredentialsException if either step fails
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getUsername(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException("Invalid username or password");
        } catch (DisabledException ex) {
            throw new InvalidCredentialsException("Account is disabled. Contact support.");
        }

        // Authentication passed — load the user to build the response
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));

        String token = jwtService.generateToken(buildClaims(user), user);
        log.info("User logged in: {}", user.getUsername());

        return buildAuthResponse(token, user, "Login successful! Welcome back.");
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    /**
     * Extra claims embedded in the JWT payload.
     * These can be read client-side without a server roundtrip.
     */
    private Map<String, Object> buildClaims(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", user.getRole().name());
        claims.put("userId", user.getId());
        claims.put("email", user.getEmail());
        return claims;
    }

    private AuthResponse buildAuthResponse(String token, User user, String message) {
        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .username(user.getUsername())
                .email(user.getEmail())
                .fullName(user.getFullName())
                .role(user.getRole())
                .message(message)
                .build();
    }
}