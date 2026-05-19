package com.example.crud.jwtspringsecurity.repository;



import com.example.crud.jwtspringsecurity.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * JPA Repository for User entities.
 *
 * Spring Data JPA auto-generates the SQL at runtime — no manual queries needed
 * for these simple lookups.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by username — used by UserDetailsService during authentication.
     */
    Optional<User> findByUsername(String username);

    /**
     * Find a user by email — useful for "forgot password" flows.
     */
    Optional<User> findByEmail(String email);

    /**
     * Check if a username is already taken (used during registration).
     */
    boolean existsByUsername(String username);

    /**
     * Check if an email is already registered.
     */
    boolean existsByEmail(String email);
}
