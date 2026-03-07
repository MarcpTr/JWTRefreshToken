package com.example.jwt_demo.dto;

import jakarta.validation.constraints.NotBlank;

public record RefreshRequest(
    @NotBlank(message = "refreshToken is required")
    String refreshToken
) {}