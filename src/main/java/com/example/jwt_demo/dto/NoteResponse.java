package com.example.jwt_demo.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record NoteResponse(
    UUID  id,
    String title,
    String content,
    LocalDateTime createdAt
) {}