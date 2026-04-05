package com.example.jwt_demo.dto;

import java.util.UUID;

public record ActiveSessionResponse(
        UUID  tokenId,
        String createdAt
) {}