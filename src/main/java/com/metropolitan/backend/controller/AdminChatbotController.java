package com.metropolitan.backend.controller;

import com.metropolitan.backend.dto.ErrorResponse;
import com.metropolitan.backend.model.ChatbotKnowledgeBase;
import com.metropolitan.backend.service.ChatbotService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/chatbot")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AdminChatbotController {

    private final ChatbotService chatbotService;

    @GetMapping
    public ResponseEntity<List<ChatbotKnowledgeBase>> getAllEntries() {
        return ResponseEntity.ok(chatbotService.getAllEntries());
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(chatbotService.getCategories());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getEntry(@PathVariable Long id) {
        return chatbotService.getEntryById(id)
                .<ResponseEntity<Object>>map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(404)
                        .body(ErrorResponse.of("Knowledge base entry not found")));
    }

    @PostMapping
    public ResponseEntity<?> createEntry(@Valid @RequestBody ChatbotKnowledgeBase entry) {
        try {
            ChatbotKnowledgeBase created = chatbotService.createEntry(entry);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateEntry(
            @PathVariable Long id,
            @Valid @RequestBody ChatbotKnowledgeBase entry
    ) {
        try {
            ChatbotKnowledgeBase updated = chatbotService.updateEntry(id, entry);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEntry(@PathVariable Long id) {
        try {
            chatbotService.deleteEntry(id);
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Entry deleted successfully");
            response.put("id", id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> error = new HashMap<>();
            error.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(
            MethodArgumentNotValidException ex
    ) {
        Map<String, String> errors = new HashMap<>();
        FieldError fieldError = ex.getBindingResult().getFieldError();
        if (fieldError != null) {
            errors.put("message", fieldError.getDefaultMessage());
            errors.put("field", fieldError.getField());
        }
        return ResponseEntity.badRequest().body(errors);
    }
}
