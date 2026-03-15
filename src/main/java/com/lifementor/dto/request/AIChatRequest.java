package com.lifementor.dto.request;

import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class AIChatRequest {

    private UUID conversationId; // Optional - null for new conversation

    @NotBlank(message = "Message is required")
    @Size(max = 5000, message = "Message cannot exceed 5000 characters")
    private String message;

    @Size(max = 100, message = "Category cannot exceed 100 characters")
    private String category = "GENERAL";

    private boolean saveToHistory = true;

    // Getters and Setters
    public UUID getConversationId() { return conversationId; }
    public void setConversationId(UUID conversationId) { this.conversationId = conversationId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public boolean isSaveToHistory() { return saveToHistory; }
    public void setSaveToHistory(boolean saveToHistory) { this.saveToHistory = saveToHistory; }
}