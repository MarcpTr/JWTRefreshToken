package com.example.jwt_demo.exception;

import java.util.Map;

public class ResourceAlreadyExistsException extends RuntimeException {

    private final Map<String, String> errors;

    public ResourceAlreadyExistsException(Map<String, String> errors) {
        super("Resource already exist");
        this.errors = errors;
    }

    public Map<String, String> getErrors() {
        return errors;
    }
}
