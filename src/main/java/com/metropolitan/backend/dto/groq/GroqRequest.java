package com.metropolitan.backend.dto.groq;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroqRequest {

    private String model;
    private List<GroqMessage> messages;

    @JsonProperty("max_tokens")
    private int maxTokens = 512;

    private double temperature = 0.3;
}
