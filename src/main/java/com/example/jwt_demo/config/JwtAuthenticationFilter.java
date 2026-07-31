package com.example.jwt_demo.config;

/**
 * Filtro que valida el JWT enviado en el header "Authorization".
 * 
 * Extrae el token, verifica que no esté expirado ni revocado y,
 * si es válido, autentica al usuario en el SecurityContext de Spring Security.
 * 
 * Se ejecuta una vez por cada petición HTTP.
 */
import com.example.jwt_demo.service.JwtService;
import com.example.jwt_demo.service.TokenService;
import com.example.jwt_demo.service.UserService;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.jwt_demo.model.Token;
import com.example.jwt_demo.model.enums.TokenType;
import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserService userService;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        jwt = authHeader.substring(7);

        try {
            username = jwtService.extractUsername(jwt);
        } catch (JwtException e) {
            filterChain.doFilter(request, response);
            return;
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            UserDetails userDetails = userService.loadUserByUsername(username);

            try {
                if (!jwtService.isTokenValid(jwt, userDetails, TokenType.ACCESS)) {
                    filterChain.doFilter(request, response);
                    return;
                }

                String refreshJti = jwtService.extractRefreshJti(jwt);

                if (refreshJti == null) {
                    filterChain.doFilter(request, response);
                    return;
                }

                Token refreshToken = tokenService.findByJti(refreshJti);

                if ( refreshToken == null || refreshToken.isExpired() || refreshToken.isRevoked()) {
                    filterChain.doFilter(request, response);
                    return;
                }

                UsernamePasswordAuthenticationToken authentication  = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (JwtException e) {
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }
        }

        filterChain.doFilter(request, response);
    }
}