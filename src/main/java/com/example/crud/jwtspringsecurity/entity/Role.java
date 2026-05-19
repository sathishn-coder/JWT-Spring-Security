package com.example.crud.jwtspringsecurity.entity;

/**
 * Roles available in the system.
 *
 * ROLE_USER  - Standard authenticated user
 * ROLE_ADMIN - Administrator with elevated privileges
 *
 * Spring Security requires the "ROLE_" prefix by default.
 */
public enum Role {
    ROLE_USER,
    ROLE_ADMIN
}
