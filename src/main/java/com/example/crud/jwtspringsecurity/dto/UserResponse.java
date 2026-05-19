package com.example.crud.jwtspringsecurity.dto;

import com.example.crud.jwtspringsecurity.entity.Role;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Safe view of a User — the password hash is never included in responses.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String username;
    private String email;
    private Role role;
    private boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
