package com.example.crud.jwtspringsecurity.dto;



import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * DTO for user login requests.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "Username is required")
    private String username;

    @NotBlank(message = "Password is required")
    private String password;
}
