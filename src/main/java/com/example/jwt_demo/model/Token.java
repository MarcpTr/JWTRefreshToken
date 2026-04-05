package com.example.jwt_demo.model;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.jwt_demo.model.enums.TokenType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID  id;

    @Column(nullable = false, unique = true, length = 512)
    private String token;

    @Column(nullable = false)
    private boolean revoked;

    @Column(nullable = false)
    private boolean expired;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TokenType tokenType;
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    private String jti;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
