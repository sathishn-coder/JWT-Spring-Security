package com.example.crud.jwtspringsecurity.dto;




import com.example.crud.jwtspringsecurity.entity.Role;
import lombok.*;

/**
 * Returned to the client after successful login or registration.
 *
 * The client stores the `token` and includes it in subsequent requests as:
 *   Authorization: Bearer <token>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;

    @Builder.Default
    private String tokenType = "Bearer";

    private String username;
    private String email;
    private String fullName;
    private Role role;
    private String message;
}
