package com.example.jwt_demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record NoteRequest(
    @NotBlank(message = "Title cannot be blank")
    @Size(min = 1, max=255)
    String title,
    String content
) {}