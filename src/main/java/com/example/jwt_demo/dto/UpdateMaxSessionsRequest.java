package com.example.jwt_demo.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

public record UpdateMaxSessionsRequest(
        @Min(value = 1, message = "Debe haber al menos 1 sesión") @Max(value = 10, message = "El máximo permitido es 10 sesiones") int maxSessions) {
}
