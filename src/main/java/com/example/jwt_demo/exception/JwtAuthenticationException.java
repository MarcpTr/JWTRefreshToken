package com.example.jwt_demo.exception;

import java.util.Map;

public class JwtAuthenticationException   extends RuntimeException {

    private final Map<String, String> errors;

    public JwtAuthenticationException  (Map<String, String> errors) {
        super("Invalid JWT");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
