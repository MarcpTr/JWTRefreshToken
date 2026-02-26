package com.example.jwt_demo.service;

import com.example.jwt_demo.dto.*;
import com.example.jwt_demo.exception.BusinessValidationException;
import com.example.jwt_demo.model.User;
import com.example.jwt_demo.model.enums.Role;
import com.example.jwt_demo.model.enums.TokenType;
import com.example.jwt_demo.repository.TokenRepository;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.jwt_demo.model.Token;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenService tokenService;
    private final TokenRepository tokenRepository;

    public AuthResponse register(RegisterRequest request) {
        Map<String, String> errors = new HashMap<>();

        if (userService.existsByEmail(request.getEmail())) {
            errors.put("email", "The email address is already registered.");
        }

        if (userService.existsByUsername(request.getUsername())) {
            errors.put("username", "The username is already registered.");
        }
        if (!errors.isEmpty()) {
            throw new BusinessValidationException(errors);
        }
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.ROLE_USER)
                .build();

        User savedUser = userService.save(user);
        var accessToken = jwtService.generateAccessToken(savedUser);
        var refreshToken = jwtService.generateRefreshToken(savedUser);
        tokenService.saveUserToken(savedUser, refreshToken, TokenType.REFRESH);
        tokenService.saveUserToken(savedUser, accessToken, TokenType.ACCESS);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse login(LoginRequest request) {
        Map<String, String> errors = new HashMap<>();

        var authToken = new UsernamePasswordAuthenticationToken(
                request.getUsernameOrEmail(), request.getPassword());
        try {
            authenticationManager.authenticate(authToken);
        } catch (AuthenticationException e) {
            errors.put("error", "access  denied");
        }
        if (!errors.isEmpty()) {
            throw new BusinessValidationException(errors);
        }

        var user = (User) userService.loadUserByUsername(request.getUsernameOrEmail());

        var accessToken = jwtService.generateAccessToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        tokenService.saveUserToken(user, accessToken, TokenType.ACCESS);
        tokenService.saveUserToken(user, refreshToken, TokenType.REFRESH);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refresh(RefreshRequest request) {
        Map<String, String> errors = new HashMap<>();

        String refreshToken = request.getRefreshToken();
        String username = "";

        try {
            username = jwtService.extractUsername(refreshToken);
        } catch (JwtException e) {
            errors.put("error", "refresh toked bad format");
        }
        if (!errors.isEmpty()) {
            throw new BusinessValidationException(errors);
        }
        User user = (User) userService.loadUserByUsername(username);

        Token storedToken = tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> {
                    errors.put("error", "token not found");
                    throw new BusinessValidationException(errors);
                });

        if (storedToken.isExpired() || storedToken.isRevoked()) {
            errors.put("error", "refresh token expired or revoked");
            throw new BusinessValidationException(errors);
        }

        if (storedToken.getTokenType() != TokenType.REFRESH) {
            errors.put("error", "Incorrect token type, expected: refresh");
            throw new BusinessValidationException(errors);
        }

        if (!jwtService.isTokenValid(refreshToken, user)) {
            errors.put("error", "Token not valid");
            throw new BusinessValidationException(errors);
        }

        String accessToken = jwtService.generateAccessToken(user);
        refreshToken = jwtService.generateRefreshToken(user);
        tokenService.saveUserToken(user, accessToken, TokenType.ACCESS);
        tokenService.saveUserToken(user, refreshToken, TokenType.REFRESH);

        return new AuthResponse(accessToken, refreshToken);
    }
}
