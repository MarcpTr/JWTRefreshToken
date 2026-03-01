package com.example.jwt_demo.controller;

import com.example.jwt_demo.dto.*;
import com.example.jwt_demo.service.AuthService;
import com.example.jwt_demo.service.SessionPolicyService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final AuthService authService;
    private final SessionPolicyService sessionPolicyService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest request) {
        return ResponseEntity.ok(authService.refresh(request));
    }
      @PutMapping("/security/max-sessions")
      @PreAuthorize("hasRole('ADMIN')")
     public ResponseEntity<Void> updateMaxSessions(
            @RequestBody UpdateMaxSessionsRequest request) {

        sessionPolicyService.updateMaxSessions(request.maxSessions());
        return ResponseEntity.noContent().build();
    }
}
