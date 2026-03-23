package com.example.jwt_demo.dto;

public record AuthResponse(
    User user,
    String accessToken,
    String refreshToken
) {
    public record User(
        long id,
        String email,
        String name
    ) {}
}