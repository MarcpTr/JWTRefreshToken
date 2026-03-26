package com.example.jwt_demo.exception;

import java.util.HashMap;
import java.util.Map;
import org.apache.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.jwt_demo.dto.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
                
        return ResponseEntity.badRequest().body(ApiResponse.fail("VALIDATION_ERROR","Faltan campos o son incorrectos", errors));
    }
    

    @ExceptionHandler(FieldValidationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleFieldValidation(FieldValidationException ex) {
        return ResponseEntity.status(HttpStatus.SC_UNPROCESSABLE_ENTITY).body(ApiResponse.fail("INVALID_INPUT","fsfsf", ex.getErrors()));
    }
    
      @ExceptionHandler(MissingFieldsException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleMissingFields(MissingFieldsException ex) {
        return ResponseEntity.status(HttpStatus.SC_BAD_REQUEST).body(ApiResponse.fail("MISSING_REQUIRED_FIELD","fsfsf", ex.getErrors()));
    }
    
        @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleResourceAlreadyExist(ResourceAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.SC_CONFLICT).body(ApiResponse.fail("RESOURCE_ALREADY_EXISTS","Estos Datos ya estan siendo utilizados", ex.getErrors()));
    }
    
        @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleInvalidCredentials(InvalidCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).body(ApiResponse.fail("INVALID_CREDENTIALS","Los datos no son correctos", ex.getErrors()));
    }
    
       @ExceptionHandler(JwtAuthenticationException .class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleJwtAuthenticationException (JwtAuthenticationException  ex) {
        return ResponseEntity.status(HttpStatus.SC_UNAUTHORIZED).body(ApiResponse.fail("TOKEN_INVALID","Este token no es valido", ex.getErrors()));
    }
}
