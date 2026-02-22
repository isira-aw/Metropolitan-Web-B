package com.metropolitan.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "chatbot_knowledge_base")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatbotKnowledgeBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Question is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String question;

    @NotBlank(message = "Answer is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String answer;

    @Column
    private String category;

    @Column(columnDefinition = "TEXT")
    private String keywords;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}
