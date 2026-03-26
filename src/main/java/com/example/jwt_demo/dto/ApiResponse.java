package com.example.jwt_demo.dto;

public record ApiResponse<T>(
        boolean success,
        T data,
        ApiError<T> error) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null);
    }

    public static <T> ApiResponse<T> fail(String code, T message) {
        return new ApiResponse<>(false, null, new ApiError<T>(code, message));
    }
}