package com.example.crud.jwtspringsecurity.service;


import com.example.crud.jwtspringsecurity.dto.UpdateUserRequest;
import com.example.crud.jwtspringsecurity.dto.UserResponse;
import com.example.crud.jwtspringsecurity.entity.User;
import com.example.crud.jwtspringsecurity.exception.ResourceNotFoundException;
import com.example.crud.jwtspringsecurity.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * User Service + UserDetailsService implementation.
 *
 * By combining both in one class:
 *  - UserDetailsService.loadUserByUsername() is used by Spring Security
 *    during authentication (called by AuthenticationManager)
 *  - CRUD methods are used by UserController (admin endpoints)
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    // ─── UserDetailsService contract ─────────────────────────────────────────

    /**
     * Called by Spring Security's AuthenticationManager during login.
     * Must return a UserDetails instance or throw UsernameNotFoundException.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    log.warn("User not found: {}", username);
                    return new UsernameNotFoundException("User not found: " + username);
                });
    }

    // ─── Admin CRUD operations ────────────────────────────────────────────────

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = findUserById(id);
        return mapToUserResponse(user);
    }

    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return mapToUserResponse(user);
    }

    @Transactional
    public UserResponse updateUser(Long id, UpdateUserRequest request) {
        User user = findUserById(id);

        // Only update fields that are present in the request (partial update)
        if (request.getFirstName() != null) user.setFirstName(request.getFirstName());
        if (request.getLastName() != null)  user.setLastName(request.getLastName());
        if (request.getEmail() != null)     user.setEmail(request.getEmail());
        if (request.getRole() != null)      user.setRole(request.getRole());
        if (request.getIsActive() != null)  user.setActive(request.getIsActive());

        User updated = userRepository.save(user);
        log.info("User updated: id={}", id);
        return mapToUserResponse(updated);
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = findUserById(id);
        userRepository.delete(user);
        log.info("User deleted: id={}", id);
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private User findUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
    }

    /**
     * Converts a User entity to a safe UserResponse DTO.
     * Never includes the password hash.
     */
    public UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole())
                .isActive(user.isActive())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
