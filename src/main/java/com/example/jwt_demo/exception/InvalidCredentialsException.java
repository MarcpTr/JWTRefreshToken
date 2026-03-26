package com.example.jwt_demo.exception;

import java.util.Map;

public class InvalidCredentialsException  extends RuntimeException {

    private final Map<String, String> errors;

    public InvalidCredentialsException (Map<String, String> errors) {
        super("Invalid credentials");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
