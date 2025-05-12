package com.example.jwt_demo.service;


import com.example.jwt_demo.dto.*;
import com.example.jwt_demo.model.Token;
import com.example.jwt_demo.model.User;
import com.example.jwt_demo.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.example.jwt_demo.model.TokenType;

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
        var user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        var savedUser = userService.save(user);

        var accessToken = jwtService.generateToken(savedUser);
        var refreshToken = jwtService.generateRefreshToken(savedUser);
        tokenService.saveUserToken(savedUser, accessToken, TokenType.ACCESS);

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
        tokenService.saveUserToken(user, accessToken, TokenType.ACCESS);
        tokenService.saveUserToken(user, refreshToken, TokenType.REFRESH);

        return new AuthResponse(accessToken, refreshToken);
    }

    public AuthResponse refresh(RefreshRequest request) {
        final String refreshToken = request.getRefreshToken();
        final String username = jwtService.extractUsername(refreshToken);
        var user = (User) userService.loadUserByUsername(username);

        var storedToken = tokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Refresh token no encontrado en base de datos"));

        if (storedToken.isExpired() || storedToken.isRevoked()) {
            throw new RuntimeException("El refresh token está expirado o ha sido revocado");
        }

        if (storedToken.getTokenType() != TokenType.REFRESH) {
            throw new RuntimeException("Token inválido: se esperaba uno de tipo REFRESH");
        }

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new RuntimeException("Refresh token inválido");
        }

        var accessToken = jwtService.generateToken(user);
        tokenService.revokeAllUserTokens(user);
        tokenService.saveUserToken(user, accessToken, TokenType.ACCESS);

        return new AuthResponse(accessToken, refreshToken);
    }
}
