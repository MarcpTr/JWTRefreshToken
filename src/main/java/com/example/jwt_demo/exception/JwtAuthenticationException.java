package com.example.jwt_demo.exception;
/**
 * Excepción personalizada para errores de validación de negocio.
 *
 * Permite devolver múltiples errores en formato campo-mensaje
 * mediante un Map<String, String>.
 */
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
