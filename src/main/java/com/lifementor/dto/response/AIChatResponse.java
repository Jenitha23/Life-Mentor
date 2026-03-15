package com.lifementor.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class AIChatResponse {

    private UUID conversationId;
    private UUID messageId;
    private String userMessage;
    private String aiResponse;
    private String category;
    private LocalDateTime timestamp;
    private int tokensUsed;
    private String aiModelUsed;

    // Constructors
    public AIChatResponse() {}

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID conversationId;
        private UUID messageId;
        private String userMessage;
        private String aiResponse;
        private String category;
        private LocalDateTime timestamp;
        private int tokensUsed;
        private String aiModelUsed;

        public Builder conversationId(UUID conversationId) {
            this.conversationId = conversationId;
            return this;
        }

        public Builder messageId(UUID messageId) {
            this.messageId = messageId;
            return this;
        }

        public Builder userMessage(String userMessage) {
            this.userMessage = userMessage;
            return this;
        }

        public Builder aiResponse(String aiResponse) {
            this.aiResponse = aiResponse;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder tokensUsed(int tokensUsed) {
            this.tokensUsed = tokensUsed;
            return this;
        }

        public Builder aiModelUsed(String aiModelUsed) {
            this.aiModelUsed = aiModelUsed;
            return this;
        }

        public AIChatResponse build() {
            AIChatResponse response = new AIChatResponse();
            response.conversationId = this.conversationId;
            response.messageId = this.messageId;
            response.userMessage = this.userMessage;
            response.aiResponse = this.aiResponse;
            response.category = this.category;
            response.timestamp = this.timestamp;
            response.tokensUsed = this.tokensUsed;
            response.aiModelUsed = this.aiModelUsed;
            return response;
        }
    }

    // Getters
    public UUID getConversationId() { return conversationId; }
    public UUID getMessageId() { return messageId; }
    public String getUserMessage() { return userMessage; }
    public String getAiResponse() { return aiResponse; }
    public String getCategory() { return category; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public int getTokensUsed() { return tokensUsed; }
    public String getAiModelUsed() { return aiModelUsed; }
}