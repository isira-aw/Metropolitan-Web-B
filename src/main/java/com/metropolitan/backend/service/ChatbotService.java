package com.metropolitan.backend.service;

import com.metropolitan.backend.dto.ChatResponse;
import com.metropolitan.backend.dto.groq.GroqMessage;
import com.metropolitan.backend.dto.groq.GroqRequest;
import com.metropolitan.backend.dto.groq.GroqResponse;
import com.metropolitan.backend.model.ChatbotKnowledgeBase;
import com.metropolitan.backend.repository.ChatbotKnowledgeBaseRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final ChatbotKnowledgeBaseRepository knowledgeBaseRepository;
    private final RestTemplate restTemplate;

    @Value("${groq.api.key:}")
    private String groqApiKey;

    @Value("${groq.api.model:openai/gpt-oss-120b}")
    private String groqModel;

    @Value("${groq.api.base-url:https://api.groq.com/openai/v1}")
    private String groqBaseUrl;

    public ChatResponse processMessage(String userMessage) {
        if (userMessage == null || userMessage.trim().isEmpty()) {
            return ChatResponse.noMatch();
        }

        List<ChatbotKnowledgeBase> knowledgeBase = knowledgeBaseRepository.findAll();

        if (groqApiKey != null && !groqApiKey.isBlank()) {
            return processWithGroq(userMessage, knowledgeBase);
        }

        // Fallback: keyword scoring when no API key is configured
        return processWithKeywordMatching(userMessage, knowledgeBase);
    }

    private ChatResponse processWithGroq(String userMessage, List<ChatbotKnowledgeBase> knowledgeBase) {
        try {
            String systemPrompt = buildSystemPrompt(knowledgeBase);

            GroqRequest request = new GroqRequest(
                groqModel,
                List.of(
                    GroqMessage.system(systemPrompt),
                    GroqMessage.user(userMessage)
                ),
                512,
                0.3
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(groqApiKey);

            HttpEntity<GroqRequest> entity = new HttpEntity<>(request, headers);

            ResponseEntity<GroqResponse> response = restTemplate.exchange(
                groqBaseUrl + "/chat/completions",
                HttpMethod.POST,
                entity,
                GroqResponse.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                String content = response.getBody().getContent();
                if (content != null && !content.isBlank()) {
                    return ChatResponse.of(content.trim(), true);
                }
            }
        } catch (Exception e) {
            log.error("Groq API call failed: {}", e.getMessage());
        }

        // Fall back to keyword matching on Groq failure
        return processWithKeywordMatching(userMessage, knowledgeBase);
    }

    private String buildSystemPrompt(List<ChatbotKnowledgeBase> knowledgeBase) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a helpful virtual assistant for Metropolitan, a leading company specializing in HVAC (central air conditioning), elevators & travelators, fire detection & protection, generators, solar energy, and ELV systems.\n\n");
        prompt.append("Answer customer questions based ONLY on the knowledge base below. ");
        prompt.append("If the question cannot be answered from the knowledge base, politely say you don't have that information and suggest the customer contact Metropolitan directly via the Contact page.\n\n");
        prompt.append("Keep answers concise, friendly, and professional.\n\n");

        if (!knowledgeBase.isEmpty()) {
            prompt.append("KNOWLEDGE BASE:\n");
            prompt.append("==============\n");
            knowledgeBase.forEach(entry -> {
                prompt.append("Q: ").append(entry.getQuestion()).append("\n");
                prompt.append("A: ").append(entry.getAnswer()).append("\n");
                if (entry.getKeywords() != null && !entry.getKeywords().isBlank()) {
                    prompt.append("Keywords: ").append(entry.getKeywords()).append("\n");
                }
                prompt.append("\n");
            });
        } else {
            prompt.append("The knowledge base is currently empty. Tell the user you are still being set up and they should contact Metropolitan directly for assistance.\n");
        }

        return prompt.toString();
    }

    private ChatResponse processWithKeywordMatching(String userMessage, List<ChatbotKnowledgeBase> allEntries) {
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

        if (bestScore >= 1 && bestMatch != null) {
            return ChatResponse.of(bestMatch.getAnswer(), true);
        }

        return ChatResponse.noMatch();
    }

    private int calculateScore(String normalizedMessage, String[] messageWords, ChatbotKnowledgeBase entry) {
        int score = 0;
        String normalizedQuestion = entry.getQuestion().toLowerCase();

        if (normalizedQuestion.equals(normalizedMessage)) {
            score += 10;
        } else if (normalizedMessage.contains(normalizedQuestion) || normalizedQuestion.contains(normalizedMessage)) {
            score += 5;
        }

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

        if (entry.getKeywords() != null && !entry.getKeywords().isEmpty()) {
            String[] keywords = entry.getKeywords().toLowerCase().split("[,\\s]+");
            for (String keyword : keywords) {
                if (!keyword.isEmpty() && keyword.length() > 2 && normalizedMessage.contains(keyword)) {
                    score += 3;
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
