package com.lifementor.dto.response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AIChatHistoryResponse {

    private UUID conversationId;
    private String title;
    private String category;
    private List<ChatMessage> messages;
    private LocalDateTime startedAt;
    private LocalDateTime lastMessageAt;

    public static class ChatMessage {
        private UUID id;
        private String role;
        private String content;
        private LocalDateTime timestamp;

        // Constructor
        public ChatMessage(UUID id, String role, String content, LocalDateTime timestamp) {
            this.id = id;
            this.role = role;
            this.content = content;
            this.timestamp = timestamp;
        }

        // Getters and Setters
        public UUID getId() { return id; }
        public void setId(UUID id) { this.id = id; }

        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }

        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }

        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }

    // Constructors
    public AIChatHistoryResponse() {}

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID conversationId;
        private String title;
        private String category;
        private List<ChatMessage> messages;
        private LocalDateTime startedAt;
        private LocalDateTime lastMessageAt;

        public Builder conversationId(UUID conversationId) {
            this.conversationId = conversationId;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder messages(List<ChatMessage> messages) {
            this.messages = messages;
            return this;
        }

        public Builder startedAt(LocalDateTime startedAt) {
            this.startedAt = startedAt;
            return this;
        }

        public Builder lastMessageAt(LocalDateTime lastMessageAt) {
            this.lastMessageAt = lastMessageAt;
            return this;
        }

        public AIChatHistoryResponse build() {
            AIChatHistoryResponse response = new AIChatHistoryResponse();
            response.conversationId = this.conversationId;
            response.title = this.title;
            response.category = this.category;
            response.messages = this.messages;
            response.startedAt = this.startedAt;
            response.lastMessageAt = this.lastMessageAt;
            return response;
        }
    }

    // Getters and Setters
    public UUID getConversationId() { return conversationId; }
    public void setConversationId(UUID conversationId) { this.conversationId = conversationId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public List<ChatMessage> getMessages() { return messages; }
    public void setMessages(List<ChatMessage> messages) { this.messages = messages; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(LocalDateTime lastMessageAt) { this.lastMessageAt = lastMessageAt; }
}