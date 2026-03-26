package com.example.jwt_demo.dto;

public record ApiError<T> (
    String code,
    String message,
    T details
){}
