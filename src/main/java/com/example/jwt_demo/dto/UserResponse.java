package com.example.jwt_demo.dto;

public record UserResponse (
    String username,
    String email,
    String role
){
    
}
