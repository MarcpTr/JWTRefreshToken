package com.example.jwt_demo.exception;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import com.example.jwt_demo.dto.ApiResponse;
import jakarta.validation.ConstraintViolationException;

@RestControllerAdvice
public class GlobalExceptionHandler {

        @ExceptionHandler(MethodArgumentNotValidException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(
                        MethodArgumentNotValidException ex) {

                Map<String, String> errors = new HashMap<>();
                ex.getBindingResult().getFieldErrors()
                                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));

                return ResponseEntity.badRequest().body(
                                ApiResponse.fail("VALIDATION_ERROR",
                                                "Required fields are missing or contain invalid values", errors));
        }

        @ExceptionHandler(ConstraintViolationException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraintViolation(
                        ConstraintViolationException ex) {

                Map<String, String> errors = new HashMap<>();

                ex.getConstraintViolations().forEach(violation -> {
                        String field = violation.getPropertyPath().toString();
                        String message = violation.getMessage();

                        errors.put(field, message);
                });

                return ResponseEntity.badRequest().body(
                                ApiResponse.fail(
                                                "VALIDATION_ERROR",
                                                "Required fields are missing or contain invalid values",
                                                errors));
        }

        @ExceptionHandler(MethodArgumentTypeMismatchException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleTypeMismatch(
                        MethodArgumentTypeMismatchException ex) {

                Map<String, String> errors = new HashMap<>();

                String field = ex.getName();
                String expectedType = ex.getRequiredType() != null
                                ? ex.getRequiredType().getSimpleName()
                                : "tipo válido";

                errors.put(field, "Debe ser de tipo: " + expectedType);

                return ResponseEntity.badRequest().body(
                                ApiResponse.fail(
                                                "VALIDATION_ERROR",
                                                "Required fields are missing or contain invalid values",
                                                errors));
        }

        @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleMethodNotSupported(
                        HttpRequestMethodNotSupportedException ex) {

                Map<String, String> errors = new HashMap<>();

                String method = ex.getMethod(); // método usado (POST, GET, etc.)
                String supportedMethods = ex.getSupportedMethods() != null
                                ? Arrays.toString(ex.getSupportedMethods())
                                : "N/A";
                errors.put("method", "Method not supported: " + method);
                errors.put("allowed", "Allowed methods: " + supportedMethods);

                return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED).body(
                                ApiResponse.fail(
                                                "METHOD_NOT_ALLOWED",
                                                "The HTTP method is not allowed for this endpoint",
                                                errors));
        }

        @ExceptionHandler(HttpMessageNotReadableException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleInvalidJson(HttpMessageNotReadableException ex) {

                return ResponseEntity.badRequest().body(
                                ApiResponse.fail(
                                                "INVALID_JSON",
                                                "Malformed JSON request",
                                                null));
        }

        @ExceptionHandler(MissingServletRequestParameterException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleMissingParams(
                        MissingServletRequestParameterException ex) {

                Map<String, String> errors = new HashMap<>();
                errors.put(ex.getParameterName(), "Parameter is required");

                return ResponseEntity.badRequest().body(
                                ApiResponse.fail("MISSING_PARAMETER", "Missing request parameter", errors));
        }

        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleAccessDenied(AccessDeniedException ex) {

                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body(ApiResponse.fail("ACCESS_DENIED", "You do not have permission", null));
        }

        // Custom Exceptions
        @ExceptionHandler(FieldValidationException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleFieldValidation(FieldValidationException ex) {
                return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY)
                                .body(ApiResponse.fail("INVALID_INPUT", "Input data is invalid", ex.getErrors()));
        }

        @ExceptionHandler(MissingFieldsException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleMissingFields(MissingFieldsException ex) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                                .body(ApiResponse.fail("MISSING_REQUIRED_FIELD", "Some required fields are missing",
                                                ex.getErrors()));
        }

        @ExceptionHandler(ResourceAlreadyExistsException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleResourceAlreadyExist(
                        ResourceAlreadyExistsException ex) {
                return ResponseEntity.status(HttpStatus.CONFLICT).body(
                                ApiResponse.fail("RESOURCE_ALREADY_EXISTS", "The provided data is already in use",
                                                ex.getErrors()));
        }

        @ExceptionHandler(InvalidCredentialsException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleInvalidCredentials(
                        InvalidCredentialsException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(ApiResponse.fail("INVALID_CREDENTIALS", "The provided credentials are invalid",
                                                ex.getErrors()));
        }

        @ExceptionHandler(JwtAuthenticationException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleJwtAuthenticationException(
                        JwtAuthenticationException ex) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                .body(ApiResponse.fail("TOKEN_INVALID", "The provided token is invalid",
                                                ex.getErrors()));
        }

        @ExceptionHandler(ResourceNotFoundException.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleResouceNotFound(ResourceNotFoundException ex) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(ApiResponse.fail("RESOURCE_NOT_FOUND", "Resource not found", ex.getErrors()));
        }

        @ExceptionHandler(Exception.class)
        public ResponseEntity<ApiResponse<Map<String, String>>> handleGenericException(Exception ex) {

                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                                .body(ApiResponse.fail(
                                                "INTERNAL_SERVER_ERROR",
                                                "An unexpected error has occurred",
                                                null));
        }
}
