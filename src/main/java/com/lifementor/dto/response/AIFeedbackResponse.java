// src/main/java/com/lifementor/dto/response/AIFeedbackResponse.java
package com.lifementor.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class AIFeedbackResponse {

    private UUID id;
    private UUID assessmentId;
    private String summary;
    private String positiveHighlights;
    private String suggestions;
    private String motivationalMessage;
    private String riskLevel;
    private boolean disclaimerShown;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public AIFeedbackResponse() {}

    public AIFeedbackResponse(UUID id, UUID assessmentId, String summary, String positiveHighlights,
                              String suggestions, String motivationalMessage, String riskLevel,
                              boolean disclaimerShown, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.assessmentId = assessmentId;
        this.summary = summary;
        this.positiveHighlights = positiveHighlights;
        this.suggestions = suggestions;
        this.motivationalMessage = motivationalMessage;
        this.riskLevel = riskLevel;
        this.disclaimerShown = disclaimerShown;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAssessmentId() {
        return assessmentId;
    }

    public void setAssessmentId(UUID assessmentId) {
        this.assessmentId = assessmentId;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getPositiveHighlights() {
        return positiveHighlights;
    }

    public void setPositiveHighlights(String positiveHighlights) {
        this.positiveHighlights = positiveHighlights;
    }

    public String getSuggestions() {
        return suggestions;
    }

    public void setSuggestions(String suggestions) {
        this.suggestions = suggestions;
    }

    public String getMotivationalMessage() {
        return motivationalMessage;
    }

    public void setMotivationalMessage(String motivationalMessage) {
        this.motivationalMessage = motivationalMessage;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
    }

    public boolean isDisclaimerShown() {
        return disclaimerShown;
    }

    public void setDisclaimerShown(boolean disclaimerShown) {
        this.disclaimerShown = disclaimerShown;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private UUID assessmentId;
        private String summary;
        private String positiveHighlights;
        private String suggestions;
        private String motivationalMessage;
        private String riskLevel;
        private boolean disclaimerShown;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder assessmentId(UUID assessmentId) {
            this.assessmentId = assessmentId;
            return this;
        }

        public Builder summary(String summary) {
            this.summary = summary;
            return this;
        }

        public Builder positiveHighlights(String positiveHighlights) {
            this.positiveHighlights = positiveHighlights;
            return this;
        }

        public Builder suggestions(String suggestions) {
            this.suggestions = suggestions;
            return this;
        }

        public Builder motivationalMessage(String motivationalMessage) {
            this.motivationalMessage = motivationalMessage;
            return this;
        }

        public Builder riskLevel(String riskLevel) {
            this.riskLevel = riskLevel;
            return this;
        }

        public Builder disclaimerShown(boolean disclaimerShown) {
            this.disclaimerShown = disclaimerShown;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public AIFeedbackResponse build() {
            return new AIFeedbackResponse(id, assessmentId, summary, positiveHighlights,
                    suggestions, motivationalMessage, riskLevel,
                    disclaimerShown, createdAt, updatedAt);
        }
    }
}