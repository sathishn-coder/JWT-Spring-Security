package com.example.crud.jwtspringsecurity.controller;


import com.example.crud.jwtspringsecurity.dto.ApiResponse;
import com.example.crud.jwtspringsecurity.dto.AuthResponse;
import com.example.crud.jwtspringsecurity.dto.LoginRequest;
import com.example.crud.jwtspringsecurity.dto.RegisterRequest;
import com.example.crud.jwtspringsecurity.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Authentication Controller — public endpoints (no JWT required).
 *
 * POST /api/auth/register  →  create account + get token
 * POST /api/auth/login     →  authenticate + get token
 *
 * @Valid triggers all @NotBlank, @Email, @Pattern constraints
 * defined in the request DTO before the method body runs.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * Register a new user account.
     *
     * Request body example:
     * {
     *   "firstName": "John",
     *   "lastName": "Doe",
     *   "username": "johndoe",
     *   "email": "john@example.com",
     *   "password": "SecurePass@1"
     * }
     */
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        AuthResponse authResponse = authService.register(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", authResponse));
    }

    /**
     * Login and receive a JWT token.
     *
     * Request body example:
     * {
     *   "username": "johndoe",
     *   "password": "SecurePass@1"
     * }
     *
     * Response:
     * {
     *   "token": "eyJhbGc...",
     *   "tokenType": "Bearer",
     *   "username": "johndoe",
     *   ...
     * }
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request) {

        AuthResponse authResponse = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", authResponse));
    }
}