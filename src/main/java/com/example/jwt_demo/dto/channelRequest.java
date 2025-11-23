package com.example.jwt_demo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class channelRequest {
    @NotBlank(message = "idChannel is required")
    private String idChannel;
}