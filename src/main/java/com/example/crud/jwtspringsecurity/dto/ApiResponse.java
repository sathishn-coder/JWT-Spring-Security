package com.example.crud.jwtspringsecurity.dto;



import lombok.*;

import java.time.LocalDateTime;

/**
 * Standard API response envelope.
 *
 * Every endpoint returns this shape so consumers always get a predictable structure:
 * {
 *   "success": true,
 *   "message": "Operation successful",
 *   "data": { ... },
 *   "timestamp": "2024-01-01T12:00:00"
 * }
 *
 * Usage:
 *   return ResponseEntity.ok(ApiResponse.success("Users fetched", userList));
 *   return ResponseEntity.badRequest().body(ApiResponse.error("User not found"));
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiResponse<T> {

    private boolean success;
    private String message;
    private T data;
    private LocalDateTime timestamp;

    // ─── Static factory helpers ──────────────────────────────────────────────

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> success(String message) {
        return success(message, null);
    }

    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build();
    }
}
