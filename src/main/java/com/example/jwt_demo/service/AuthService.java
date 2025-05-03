package com.example.jwt_demo.service;


import com.example.jwt_demo.dto.*;
import com.example.jwt_demo.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final TokenService tokenService;

    public AuthResponse register(RegisterRequest request) {
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        var savedUser = userService.save(user);

        var accessToken = jwtService.generateToken(savedUser);
        var refreshToken = jwtService.generateRefreshToken(savedUser);
        tokenService.saveUserToken(savedUser, accessToken);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse login(LoginRequest request) {
        var authToken = new UsernamePasswordAuthenticationToken(
                request.getUsernameOrEmail(), request.getPassword());

        authenticationManager.authenticate(authToken);

        var user = (User) userService.loadUserByUsername(request.getUsernameOrEmail());

        tokenService.revokeAllUserTokens(user);

        var accessToken = jwtService.generateToken(user);
        var refreshToken = jwtService.generateRefreshToken(user);
        tokenService.saveUserToken(user, accessToken);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refresh(RefreshRequest request) {
        final String refreshToken = request.getRefreshToken();
        final String username = jwtService.extractUsername(refreshToken);

        var user = (User) userService.loadUserByUsername(username);

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new RuntimeException("Refresh token inválido");
        }

        var accessToken = jwtService.generateToken(user);
        tokenService.revokeAllUserTokens(user);
        tokenService.saveUserToken(user, accessToken);

        return new AuthResponse(accessToken, refreshToken);
    }
}
