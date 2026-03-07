package com.example.jwt_demo.dto;

public record AuthResponse(
    String accessToken,
    String refreshToken
) {}