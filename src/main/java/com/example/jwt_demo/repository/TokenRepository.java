package com.example.jwt_demo.repository;


import com.example.jwt_demo.model.Token;
import com.example.jwt_demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TokenRepository extends JpaRepository<Token, Long> {
    List<Token> findAllByUserAndExpiredFalseAndRevokedFalse(User user);
    Optional<Token> findByToken(String token);
}
