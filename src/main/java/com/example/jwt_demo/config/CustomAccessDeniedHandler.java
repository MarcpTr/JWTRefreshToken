package com.example.jwt_demo.config;

import java.io.IOException;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import com.example.jwt_demo.dto.ApiError;
import com.example.jwt_demo.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {
    private final ObjectMapper mapper= new ObjectMapper();
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
                response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                response.setContentType(("application/json"));
                ApiResponse<Void> body= new ApiResponse<>(false, null, new ApiError<>("FORBIDDEN", "Acces denied", null));
                mapper.writeValue(response.getOutputStream(), body);
            }
    
}
