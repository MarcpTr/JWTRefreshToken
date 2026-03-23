package com.example.jwt_demo.dto;

public record RefreshResponse(
   String accessToken,
   String refreshToken
) {}