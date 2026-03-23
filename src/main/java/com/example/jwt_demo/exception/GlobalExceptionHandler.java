package com.example.jwt_demo.exception;
/**
 * Manejador global de excepciones REST.
 *
 * Captura errores de validación (DTO y reglas de negocio)
 * y devuelve respuestas HTTP 400 con el detalle de los errores.
 */
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Errores de validación del DTO @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
                
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(FieldValidationException.class)
    public ResponseEntity<Map<String, String>> handleFieldValidation(FieldValidationException ex) {
        return ResponseEntity.status(HttpStatus.SC_UNPROCESSABLE_ENTITY).body(ex.getErrors());
    }
      @ExceptionHandler(MissingFieldsException.class)
    public ResponseEntity<Map<String, String>> handleMissingFields(MissingFieldsException ex) {
        return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(ex.getErrors());
    }
        @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handleResourceAlreadyExist(ResourceAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.SC_CONFLICT).body(ex.getErrors());
    }
        @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<Map<String, String>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).body(ex.getErrors());
    }
       @ExceptionHandler(JwtAuthenticationException .class)
    public ResponseEntity<Map<String, String>> handleJwtAuthenticationException (JwtAuthenticationException  ex) {
        return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).body(ex.getErrors());
    }
}
