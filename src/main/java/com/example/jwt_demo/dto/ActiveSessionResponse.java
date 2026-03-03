package com.example.jwt_demo.dto;

public record ActiveSessionResponse(
        Long tokenId,
        String createdAt
) {}