package com.example.jwt_demo.service;

import com.example.jwt_demo.dto.NoteRequest;
import com.example.jwt_demo.dto.NoteResponse;
import com.example.jwt_demo.model.Note;
import com.example.jwt_demo.model.User;
import com.example.jwt_demo.repository.NoteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;

    public void createNote(NoteRequest request, User user) {
        Note note = Note.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();
        noteRepository.save(note);
    }

    public List<NoteResponse> getUserNotes(User user) {
        return noteRepository.findAllByUser(user).stream()
                .map(n -> new NoteResponse(n.getId(), n.getTitle(), n.getContent(), n.getCreatedAt()))
                .toList();
    }
}