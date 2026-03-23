package com.example.jwt_demo.exception;
/**
 * Excepción personalizada para errores de validación de negocio.
 *
 * Permite devolver múltiples errores en formato campo-mensaje
 * mediante un Map<String, String>.
 */
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
