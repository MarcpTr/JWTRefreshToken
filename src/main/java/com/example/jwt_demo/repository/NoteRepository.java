package com.example.jwt_demo.repository;


import com.example.jwt_demo.model.Note;
import com.example.jwt_demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findAllByUser(User user);
}