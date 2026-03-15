package com.lifementor.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class WellbeingAlertResponse {

    private UUID id;
    private String level; // INFO, WARNING, CRITICAL
    private String message;
    private String suggestedAction;
    private boolean resolved;
    private LocalDateTime createdAt;

    // Constructors
    public WellbeingAlertResponse() {}

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String level;
        private String message;
        private String suggestedAction;
        private boolean resolved;
        private LocalDateTime createdAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder level(String level) { this.level = level; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder suggestedAction(String suggestedAction) { this.suggestedAction = suggestedAction; return this; }
        public Builder resolved(boolean resolved) { this.resolved = resolved; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public WellbeingAlertResponse build() {
            WellbeingAlertResponse response = new WellbeingAlertResponse();
            response.id = this.id;
            response.level = this.level;
            response.message = this.message;
            response.suggestedAction = this.suggestedAction;
            response.resolved = this.resolved;
            response.createdAt = this.createdAt;
            return response;
        }
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getSuggestedAction() { return suggestedAction; }
    public void setSuggestedAction(String suggestedAction) { this.suggestedAction = suggestedAction; }

    public boolean isResolved() { return resolved; }
    public void setResolved(boolean resolved) { this.resolved = resolved; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}