package com.example.jwt_demo.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RegisterRequest {
    @Pattern(regexp = "^[a-zA-Z0-9]{2,10}$", message = "The username must contain between 2 and 10 characters and can only include letters and numbers.")
    private String username;
    @Email(message = "email not valid")
    @NotBlank(message = "email is required")
    private String email;
  @Pattern(
    regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z0-9]).{8,}$",
    message = "The password must be at least 8 characters long and include uppercase letters, lowercase letters, numbers, and a special character.") 
    private String password;
}