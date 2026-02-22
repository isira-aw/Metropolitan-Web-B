package com.metropolitan.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatResponse {

    private String message;
    private boolean matched;

    public static ChatResponse of(String message, boolean matched) {
        return new ChatResponse(message, matched);
    }

    public static ChatResponse noMatch() {
        return new ChatResponse(
            "I'm sorry, I don't have information about that. Please contact us directly at our Contact page and we'll be happy to help.",
            false
        );
    }
}
