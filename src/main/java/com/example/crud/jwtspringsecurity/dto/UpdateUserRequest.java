package com.example.crud.jwtspringsecurity.dto;


import com.example.crud.jwtspringsecurity.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

/**
 * DTO for admin-level user updates.
 * All fields are optional — only non-null fields will be applied.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateUserRequest {

    @Size(min = 2, max = 50)
    private String firstName;

    @Size(min = 2, max = 50)
    private String lastName;

    @Email(message = "Must be a valid email address")
    private String email;

    private Role role;

    private Boolean isActive;
}
