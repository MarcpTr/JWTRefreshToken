package com.example.jwt_demo.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import com.example.jwt_demo.dto.ApiResponse;
import com.example.jwt_demo.dto.UserResponse;
import com.example.jwt_demo.model.User;

@RestController
@RequestMapping("/api/test")
public class TestController {
    @GetMapping("/public")
    public ResponseEntity<ApiResponse<String>> publicEndpoint() {
        return ResponseEntity.ok(ApiResponse.ok("Este endpoint es público, cualquiera puede acceder."));

    }

    @GetMapping("/user")
    public ResponseEntity<ApiResponse<String>> userEndpoint() {
        return ResponseEntity.ok(ApiResponse.ok("Este endpoint requiere ROLE_USER o ROLE_ADMIN."));

    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<String>> adminEndpoint() {
        return ResponseEntity.ok(ApiResponse.ok("Este endpoint requiere ROLE_ADMIN exclusivamente."));
    }

    @GetMapping("/whoami")
    public ResponseEntity<ApiResponse<UserResponse>> whoAmI(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(new UserResponse(user.getUsername(), user.getEmail(), user.getRole().name())));

      
    }
}
