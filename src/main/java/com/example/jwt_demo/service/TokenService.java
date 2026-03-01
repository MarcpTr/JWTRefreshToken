package com.example.jwt_demo.service;

import com.example.jwt_demo.model.Token;
import com.example.jwt_demo.model.User;
import com.example.jwt_demo.model.enums.TokenType;
import com.example.jwt_demo.repository.TokenRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenRepository tokenRepository;

    public void saveUserToken(User user, String jwtToken, TokenType tokenType) {
        var token = Token.builder()
                .user(user)
                .token(jwtToken)
                .tokenType(tokenType)
                .expired(false)
                .revoked(false)
                .build();
        tokenRepository.save(token);
    }

    public void revokeAllUserTokens(User user) {
        List<Token> validUserTokens = tokenRepository.findAllByUserAndExpiredFalseAndRevokedFalse(user).stream()
                .filter(t -> t.getTokenType() == TokenType.ACCESS)
                .toList();
        if (validUserTokens.isEmpty())
            return;

        validUserTokens.forEach(token -> {
            token.setExpired(true);
            token.setRevoked(true);
        });
        tokenRepository.saveAll(validUserTokens);
    }

    public Token findByToken(String token) {
        return tokenRepository.findByToken(token)
                .orElseThrow(() -> new BadCredentialsException("JWT revocado o inexistente"));
    }

    @Transactional
    public void enforceSessionLimit(User user, int maxSessions) {

        List<Token> activeRefreshTokens = tokenRepository
                .findAllByUserAndTokenTypeAndExpiredFalseAndRevokedFalse(
                        user,
                        TokenType.REFRESH);

        if (activeRefreshTokens.size() < maxSessions) {
            return;
        }

        // Ordenar por fecha más antigua primero
        activeRefreshTokens.sort(Comparator.comparing(Token::getCreatedAt));

        int tokensToRevoke = activeRefreshTokens.size() - maxSessions + 1;

        for (int i = 0; i < tokensToRevoke; i++) {
            Token token = activeRefreshTokens.get(i);
            token.setExpired(true);
            token.setRevoked(true);
        }

        tokenRepository.saveAll(activeRefreshTokens);
    }
}
