package com.example.crud.jwtspringsecurity.controller;


import com.example.crud.jwtspringsecurity.dto.ApiResponse;
import com.example.crud.jwtspringsecurity.dto.UserResponse;
import com.example.crud.jwtspringsecurity.entity.User;
import com.example.crud.jwtspringsecurity.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * User Controller — endpoints accessible to authenticated users (USER and ADMIN).
 *
 * @AuthenticationPrincipal injects the currently authenticated User directly
 * from the SecurityContext — no need to extract from headers or JWT manually.
 */
@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Get current user's profile.
     *
     * @AuthenticationPrincipal resolves to the User entity because our
     * UserDetailsService returns User, which implements UserDetails.
     */
    @GetMapping("/profile")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<UserResponse>> getMyProfile(
            @AuthenticationPrincipal User currentUser) {

        UserResponse profile = userService.mapToUserResponse(currentUser);
        return ResponseEntity.ok(ApiResponse.success("Profile fetched successfully", profile));
    }

    /**
     * A simple endpoint to confirm the user is authenticated.
     */
    @GetMapping("/hello")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<ApiResponse<String>> hello(
            @AuthenticationPrincipal User currentUser) {

        String message = String.format("Hello %s! You are authenticated as %s",
                currentUser.getFullName(), currentUser.getRole().name());
        return ResponseEntity.ok(ApiResponse.success(message, null));
    }
}
