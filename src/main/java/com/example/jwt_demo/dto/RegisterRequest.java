package com.example.jwt_demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RegisterRequest(

  @Pattern(
    regexp = "^[a-zA-Z0-9](?:[a-zA-Z0-9_]{0,8}[a-zA-Z0-9])?$",
      message = "The username must contain between 2 and 10 characters and can only include letters and numbers."
  )
  String username,

  @Email(message = "email not valid")
  @NotBlank(message = "email is required")
  String email,

  @Pattern(
      regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$",
      message = "The password must be at least 8 characters long and include uppercase letters, lowercase letters, numbers, and a special character."
  )
  String password

) {}