package com.example.jwt_demo.controller;


import com.example.jwt_demo.dto.NoteRequest;
import com.example.jwt_demo.dto.NoteResponse;
import com.example.jwt_demo.model.User;
import com.example.jwt_demo.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@RequiredArgsConstructor
public class NoteController {

    private final NoteService noteService;

    @PostMapping
    public ResponseEntity<Void> createNote(@RequestBody NoteRequest request,
                                           @AuthenticationPrincipal User user) {
        noteService.createNote(request, user);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<NoteResponse>> getUserNotes(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(noteService.getUserNotes(user));
    }
}
