package com.metropolitan.backend.service;

import com.metropolitan.backend.dto.ChatResponse;
import com.metropolitan.backend.model.ChatbotKnowledgeBase;
import com.metropolitan.backend.repository.ChatbotKnowledgeBaseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final ChatbotKnowledgeBaseRepository knowledgeBaseRepository;

    private static final int MATCH_THRESHOLD = 1;

    public ChatResponse processMessage(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return ChatResponse.noMatch();
        }

        List<ChatbotKnowledgeBase> allEntries = knowledgeBaseRepository.findAll();
        if (allEntries.isEmpty()) {
            return ChatResponse.noMatch();
        }

        String normalizedMessage = userMessage.toLowerCase().trim();
        String[] messageWords = normalizedMessage.split("\\s+");

        ChatbotKnowledgeBase bestMatch = null;
        int bestScore = 0;

        for (ChatbotKnowledgeBase entry : allEntries) {
            int score = calculateScore(normalizedMessage, messageWords, entry);
            if (score > bestScore) {
                bestScore = score;
                bestMatch = entry;
            }
        }

        if (bestScore >= MATCH_THRESHOLD && bestMatch != null) {
            return ChatResponse.of(bestMatch.getAnswer(), true);
        }

        return ChatResponse.noMatch();
    }

    private int calculateScore(String normalizedMessage, String[] messageWords, ChatbotKnowledgeBase entry) {
        int score = 0;

        // Check question match
        String normalizedQuestion = entry.getQuestion().toLowerCase();
        if (normalizedQuestion.equals(normalizedMessage)) {
            score += 10;
        } else if (normalizedMessage.contains(normalizedQuestion) || normalizedQuestion.contains(normalizedMessage)) {
            score += 5;
        }

        // Score based on word overlap with question
        String[] questionWords = normalizedQuestion.split("\\s+");
        for (String word : messageWords) {
            if (word.length() > 2) {
                for (String questionWord : questionWords) {
                    if (questionWord.equals(word)) {
                        score += 2;
                    } else if (questionWord.contains(word) || word.contains(questionWord)) {
                        score += 1;
                    }
                }
            }
        }

        // Score based on keyword matches
        if (entry.getKeywords() != null && !entry.getKeywords().isEmpty()) {
            String[] keywords = entry.getKeywords().toLowerCase().split("[,\\s]+");
            for (String keyword : keywords) {
                if (!keyword.isEmpty() && keyword.length() > 2) {
                    if (normalizedMessage.contains(keyword)) {
                        score += 3;
                    }
                }
            }
        }

        return score;
    }

    // CRUD operations for admin
    public List<ChatbotKnowledgeBase> getAllEntries() {
        return knowledgeBaseRepository.findAllByOrderByCategoryAscCreatedAtDesc();
    }

    public Optional<ChatbotKnowledgeBase> getEntryById(Long id) {
        return knowledgeBaseRepository.findById(id);
    }

    public ChatbotKnowledgeBase createEntry(ChatbotKnowledgeBase entry) {
        return knowledgeBaseRepository.save(entry);
    }

    public ChatbotKnowledgeBase updateEntry(Long id, ChatbotKnowledgeBase details) {
        ChatbotKnowledgeBase entry = knowledgeBaseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Knowledge base entry not found with id: " + id));

        entry.setQuestion(details.getQuestion());
        entry.setAnswer(details.getAnswer());
        entry.setCategory(details.getCategory());
        entry.setKeywords(details.getKeywords());

        return knowledgeBaseRepository.save(entry);
    }

    public void deleteEntry(Long id) {
        if (!knowledgeBaseRepository.existsById(id)) {
            throw new RuntimeException("Knowledge base entry not found with id: " + id);
        }
        knowledgeBaseRepository.deleteById(id);
    }

    public List<String> getCategories() {
        return knowledgeBaseRepository.findAll()
                .stream()
                .map(ChatbotKnowledgeBase::getCategory)
                .filter(Objects::nonNull)
                .filter(c -> !c.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
