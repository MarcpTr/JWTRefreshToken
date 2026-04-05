package com.example.jwt_demo.repository;

import com.example.jwt_demo.model.Token;
import com.example.jwt_demo.model.User;
import com.example.jwt_demo.model.enums.TokenType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TokenRepository extends JpaRepository<Token, UUID> {
        Optional<Token> findByJti(String jti);

        List<Token> findAllByUserAndExpiredFalseAndRevokedFalse(User user);

        List<Token> findAllByUserAndTokenTypeAndExpiredFalseAndRevokedFalse(
                        User user,
                        TokenType tokenType);

        Optional<Token> findByToken(String token);

        List<Token> findAllByUserAndTokenTypeAndExpiredFalseAndRevokedFalseOrderByCreatedAtDesc(
                        User user,
                        TokenType tokenType);

}
