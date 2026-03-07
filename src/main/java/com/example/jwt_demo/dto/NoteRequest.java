package com.example.jwt_demo.dto;

public record NoteRequest(
    String title,
    String content
) {}