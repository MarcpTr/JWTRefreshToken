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

    public NoteResponse createNote(NoteRequest request, User user) {
        Note note = Note.builder()
                .title(request.title())
                .content(request.content())
                .createdAt(LocalDateTime.now())
                .user(user)
                .build();
                note= noteRepository.save(note);
        return new NoteResponse(note.getId(), note.getTitle(), note.getContent(), note.getCreatedAt());
    }

    public List<NoteResponse> getUserNotes(User user) {
        return noteRepository.findAllByUser(user).stream()
                .map(n -> new NoteResponse(n.getId(), n.getTitle(), n.getContent(), n.getCreatedAt()))
                .toList();
    }
}