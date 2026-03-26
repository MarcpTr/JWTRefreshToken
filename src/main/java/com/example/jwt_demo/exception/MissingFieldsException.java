package com.example.jwt_demo.exception;

import java.util.Map;

public class MissingFieldsException extends RuntimeException {

    private final Map<String, String> errors;

    public MissingFieldsException(Map<String, String> errors) {
        super("Missing fields");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
