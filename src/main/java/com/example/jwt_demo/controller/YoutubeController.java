package com.example.jwt_demo.controller;

import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.jwt_demo.dto.channelRequest;
import com.example.jwt_demo.service.YoutubeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/youtube")
@RequiredArgsConstructor
@CrossOrigin
public class YoutubeController {

          private final YoutubeService youtubeService;

    @GetMapping("/youtube")
    public ResponseEntity<List<Map<String, String>>> youtube(@Valid @RequestBody channelRequest request) {
        try {
            return ResponseEntity.ok(youtubeService.getChannelVideos(request.getIdChannel()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
  @GetMapping("/channels")
    public ResponseEntity<?> searchChannels(@RequestBody String name) {
        try {
            return ResponseEntity.ok(youtubeService.searchChannelsByName(name));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }
}