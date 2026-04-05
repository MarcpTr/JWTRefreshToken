package com.example.jwt_demo.service;

import com.example.jwt_demo.dto.*;
import com.example.jwt_demo.exception.InvalidCredentialsException;
import com.example.jwt_demo.exception.JwtAuthenticationException;
import com.example.jwt_demo.exception.ResourceAlreadyExistsException;
import com.example.jwt_demo.model.User;
import com.example.jwt_demo.model.enums.Role;
import com.example.jwt_demo.model.enums.TokenType;
import com.example.jwt_demo.repository.TokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
    private final SessionPolicyService sessionPolicyService;

    public AuthResponse register(RegisterRequest request) {
        Map<String, String> errors = new HashMap<>();

        if (userService.existsByEmail(request.email())) {
            errors.put("email", "The email address is already registered.");
        }

        if (userService.existsByUsername(request.username())) {
            errors.put("username", "The username is already registered.");
        }

        if (!errors.isEmpty()) {
            throw new ResourceAlreadyExistsException(errors);
        }

        User user = User.builder()
                .username(request.username())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ROLE_USER)
                .build();

        User savedUser = userService.save(user);

        tokenService.revokeAllUserTokens(savedUser);

        String refreshToken = jwtService.generateRefreshToken(savedUser);
        String accessToken = jwtService.generateAccessToken(savedUser, refreshToken);

        tokenService.saveUserToken(savedUser, refreshToken, TokenType.REFRESH);

        return new AuthResponse(
                new AuthResponse.User(savedUser.getId(), savedUser.getUsername(), savedUser.getEmail()),
                accessToken,
                refreshToken);
    }

    public AuthResponse login(LoginRequest request) {

        Map<String, String> errors = new HashMap<>();

        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                request.usernameOrEmail(),
                request.password());

        Authentication auth;
        try {
            auth = authenticationManager.authenticate(authRequest);
        } catch (AuthenticationException e) {
            errors.put("error", "access denied, bad credentials");
            throw new InvalidCredentialsException(errors);
        }

        User user = (User) auth.getPrincipal();

        int maxSessions = sessionPolicyService.getMaxSessions();

        tokenService.enforceSessionLimit(user, maxSessions);

        String refreshToken = jwtService.generateRefreshToken(user);
        String accessToken = jwtService.generateAccessToken(user, refreshToken);

        tokenService.saveUserToken(user, refreshToken, TokenType.REFRESH);

        return new AuthResponse(
                new AuthResponse.User(user.getId(), user.getUsername(), user.getEmail()),
                accessToken,
                refreshToken);
    }

    public RefreshResponse refresh(RefreshRequest request) {
        String refreshToken = request.refreshToken();

        Claims claims;
        try {
            claims = jwtService.extractClaim(refreshToken, c -> c);
        } catch (JwtException e) {
            throw new JwtAuthenticationException(Map.of("error", "Invalid refresh token"));
        }

        String username = claims.getSubject();
        String jti = claims.getId();

        User user = (User) userService.loadUserByUsername(username);

        Token storedToken = tokenRepository.findByJti(jti)
                .orElseThrow(() -> new JwtAuthenticationException(Map.of("error", "Token not found")));

        if (storedToken.isExpired() || storedToken.isRevoked()) {
            throw new JwtAuthenticationException(Map.of("error", "Token expired or revoked"));
        }

        if (storedToken.getTokenType() != TokenType.REFRESH) {
            throw new JwtAuthenticationException(Map.of("error", "Invalid token type"));
        }

        if (!jwtService.isTokenValid(refreshToken, user, TokenType.REFRESH)) {
            throw new JwtAuthenticationException(Map.of("error", "Invalid token"));
        }

        tokenService.revokeToken(refreshToken);

        String newRefreshToken = jwtService.generateRefreshToken(user);
        String newAccessToken = jwtService.generateAccessToken(user, newRefreshToken);

        tokenService.saveUserToken(user, newRefreshToken, TokenType.REFRESH);

        return new RefreshResponse(newAccessToken, newRefreshToken);
    }
}
