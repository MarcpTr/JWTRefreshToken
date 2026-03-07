package com.example.jwt_demo.dto;

import java.time.LocalDateTime;

public record NoteResponse(
    Long id,
    String title,
    String content,
    LocalDateTime createdAt
) {}