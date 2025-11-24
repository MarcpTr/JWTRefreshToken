package com.example.jwt_demo.controller;

import com.example.jwt_demo.dto.*;
import com.example.jwt_demo.service.AuthService;
import com.example.jwt_demo.service.YoutubeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin
public class AuthController {

    private final AuthService authService;

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


    @GetMapping("/youtube")
    public ResponseEntity<List<Map<String, String>>> youtube(@Valid @RequestBody channelRequest request) {
        try {
            return ResponseEntity.ok(youtubeService.getChannelVideos(request.getIdChannel()));
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }
        private final YoutubeService youtubeService;
  @GetMapping("/channels")
    public ResponseEntity<?> searchChannels(@RequestBody String name) {
        try {
            return ResponseEntity.ok(youtubeService.searchChannelsByName(name));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}
