package com.example.jwt_demo.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.jwt_demo.dto.ActiveSessionResponse;
import com.example.jwt_demo.dto.ApiResponse;
import com.example.jwt_demo.model.User;
import com.example.jwt_demo.service.TokenService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {

    private final TokenService tokenService;

    @GetMapping
    public  ResponseEntity<ApiResponse<List<ActiveSessionResponse>>> getMySessions(
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
                return ResponseEntity.ok(ApiResponse.ok(tokenService.getActiveSessions(user)));

    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> revokeSession(
            @PathVariable Long id,
            Authentication authentication) {

        User user = (User) authentication.getPrincipal();
        tokenService.revokeSession(user, id);
                
        return ResponseEntity.noContent().build();
    }
}