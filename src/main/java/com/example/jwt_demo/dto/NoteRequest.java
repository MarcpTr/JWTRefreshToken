package com.example.jwt_demo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NoteRequest {
    private String title;
    private String content;
}