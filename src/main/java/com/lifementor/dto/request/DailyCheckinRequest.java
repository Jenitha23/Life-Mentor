package com.lifementor.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class DailyCheckinRequest {

    @NotNull(message = "Question ID is required")
    private UUID questionId;

    @NotNull(message = "Answer is required")
    @Size(max = 5000, message = "Answer cannot exceed 5000 characters")
    private String answer;

    private String metadata; // Optional JSON string for additional data

    // Getters and Setters
    public UUID getQuestionId() { return questionId; }
    public void setQuestionId(UUID questionId) { this.questionId = questionId; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
}