package com.example.jwt_demo.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.example.jwt_demo.model.User;
import com.example.jwt_demo.model.enums.TokenType;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    @Value("${jwt.refresh-expiration}")
    private long refreshExpiration;
    @Value("${jwt.issuer}")
    private String issuer;
    @Value("${jwt.audience}")
    private String audience;
    private Key key;

    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    public String extractUsername(Claims tokenClaims) {
        return extractClaim(tokenClaims, Claims::getSubject);
    }

    public String extractJti(Claims tokenClaims) {
        return extractClaim(tokenClaims, Claims::getId);
    }

    public Date extractExpiration(Claims tokenClaims) {
        return extractClaim(tokenClaims, Claims::getExpiration);
    }

    public String extractRefreshJti(Claims tokenClaims) {
        return extractClaim(tokenClaims, claims -> claims.get("refresh_jti", String.class));
    }

    public boolean isTokenValid(Claims tokenClaims, TokenType expectedType) {
        try {

            String typeStr = tokenClaims.get("type", String.class);

            if (typeStr == null) {
                return false;
            }

            TokenType type = TokenType.valueOf(typeStr);

            return type.equals(expectedType);

        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public <T> T extractClaim(Claims tokenClaims, Function<Claims, T> resolver) throws JwtException {
        return resolver.apply(tokenClaims);
    }

    public Claims extractAllClaims(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .requireIssuer(issuer)
                .requireAudience(audience)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return generateToken(userDetails, refreshExpiration, TokenType.REFRESH, null);
    }

    public String generateAccessToken(UserDetails userDetails, Claims tokenClaimss) {

        String refreshJti = extractJti(tokenClaimss);

        return generateToken(userDetails, jwtExpiration, TokenType.ACCESS, refreshJti);
    }

    private String generateToken(UserDetails userDetails,
            long expirationTime,
            TokenType type,
            String refreshJti) {

        JwtBuilder builder = Jwts.builder()
                .claim("role", ((User) userDetails).getRole().name())
                .claim("type", type.name())
                .setHeaderParam("typ", "JWT")
                .setHeaderParam("version", "v1")
                .setId(UUID.randomUUID().toString())
                .setIssuer(issuer)
                .setSubject(userDetails.getUsername())
                .setAudience(audience)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime));

        if (type == TokenType.ACCESS && refreshJti != null) {
            builder.claim("refresh_jti", refreshJti);
        }

        return builder
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

}
