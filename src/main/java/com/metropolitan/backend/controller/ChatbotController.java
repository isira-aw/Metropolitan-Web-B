package com.metropolitan.backend.controller;

import com.metropolitan.backend.dto.ChatRequest;
import com.metropolitan.backend.dto.ChatResponse;
import com.metropolitan.backend.service.ChatbotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/chat")
    public ResponseEntity<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        ChatResponse response = chatbotService.processMessage(request.getMessage());
        return ResponseEntity.ok(response);
    }
}
