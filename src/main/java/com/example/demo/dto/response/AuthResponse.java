package com.example.demo.dto.response;

import com.example.demo.entity.AdminUser;

public record AuthResponse(
        String accessToken,
        UserResponse user
) {
    public record UserResponse(
            Long userId,
            String email,
            String name
    ) {}

    public static AuthResponse of(String accessToken, AdminUser user) {
        return new AuthResponse(
                accessToken,
                new UserResponse(user.getId(), user.getEmail(), user.getName())
        );
    }
}