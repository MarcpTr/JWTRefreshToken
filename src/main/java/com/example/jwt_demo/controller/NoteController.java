package com.example.jwt_demo.controller;

import com.example.jwt_demo.dto.ApiResponse;
import com.example.jwt_demo.dto.NoteRequest;
import com.example.jwt_demo.dto.NoteResponse;
import com.example.jwt_demo.model.User;
import com.example.jwt_demo.service.NoteService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
@CrossOrigin
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<ApiResponse<NoteResponse>> createNote(@Valid @RequestBody NoteRequest request,
                                           @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(noteService.createNote(request, user)));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NoteResponse>>> getUserNotes(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(ApiResponse.ok(noteService.getUserNotes(user)));
    }
}
