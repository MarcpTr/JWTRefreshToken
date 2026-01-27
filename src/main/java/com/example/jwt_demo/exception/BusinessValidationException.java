package com.example.jwt_demo.exception;

import java.util.Map;

public class BusinessValidationException extends RuntimeException {

    private final Map<String, String> errors;

    public BusinessValidationException(Map<String, String> errors) {
        super("Validation failed");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
