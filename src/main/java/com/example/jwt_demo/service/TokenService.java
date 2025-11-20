package com.example.jwt_demo.service;

import com.example.jwt_demo.model.Token;
import com.example.jwt_demo.model.TokenType;
import com.example.jwt_demo.model.User;
import com.example.jwt_demo.repository.TokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
}
