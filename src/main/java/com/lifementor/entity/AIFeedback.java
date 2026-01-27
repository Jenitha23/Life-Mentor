// src/main/java/com/lifementor/entity/AIFeedback.java
package com.lifementor.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ai_feedback")
public class AIFeedback {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "CHAR(36)")
    private UUID id;

    @OneToOne
    @JoinColumn(name = "assessment_id", nullable = false, unique = true)
    private LifestyleAssessment assessment;

    @Column(name = "summary", columnDefinition = "TEXT", nullable = false)
    private String summary;

    @Column(name = "positive_highlights", columnDefinition = "TEXT")
    private String positiveHighlights;

    @Column(name = "suggestions", columnDefinition = "TEXT", nullable = false)
    private String suggestions;

    @Column(name = "motivational_message", columnDefinition = "TEXT")
    private String motivationalMessage;

    @Column(name = "ai_model_version")
    private String aiModelVersion;

    @Column(name = "disclaimer_shown", nullable = false)
    private boolean disclaimerShown = true;

    @Column(name = "risk_level")
    private String riskLevel; // LOW, MEDIUM, HIGH (for wellbeing concerns)

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public AIFeedback() {}

    public AIFeedback(LifestyleAssessment assessment, String summary, String suggestions) {
        this.assessment = assessment;
        this.summary = summary;
        this.suggestions = suggestions;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public LifestyleAssessment getAssessment() {
        return assessment;
    }

    public void setAssessment(LifestyleAssessment assessment) {
        this.assessment = assessment;
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

    public String getAiModelVersion() {
        return aiModelVersion;
    }

    public void setAiModelVersion(String aiModelVersion) {
        this.aiModelVersion = aiModelVersion;
    }

    public boolean isDisclaimerShown() {
        return disclaimerShown;
    }

    public void setDisclaimerShown(boolean disclaimerShown) {
        this.disclaimerShown = disclaimerShown;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(String riskLevel) {
        this.riskLevel = riskLevel;
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
        private LifestyleAssessment assessment;
        private String summary;
        private String positiveHighlights;
        private String suggestions;
        private String motivationalMessage;
        private String aiModelVersion;
        private boolean disclaimerShown = true;
        private String riskLevel;

        public Builder assessment(LifestyleAssessment assessment) {
            this.assessment = assessment;
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

        public Builder aiModelVersion(String aiModelVersion) {
            this.aiModelVersion = aiModelVersion;
            return this;
        }

        public Builder disclaimerShown(boolean disclaimerShown) {
            this.disclaimerShown = disclaimerShown;
            return this;
        }

        public Builder riskLevel(String riskLevel) {
            this.riskLevel = riskLevel;
            return this;
        }

        public AIFeedback build() {
            AIFeedback feedback = new AIFeedback();
            feedback.setAssessment(this.assessment);
            feedback.setSummary(this.summary);
            feedback.setPositiveHighlights(this.positiveHighlights);
            feedback.setSuggestions(this.suggestions);
            feedback.setMotivationalMessage(this.motivationalMessage);
            feedback.setAiModelVersion(this.aiModelVersion);
            feedback.setDisclaimerShown(this.disclaimerShown);
            feedback.setRiskLevel(this.riskLevel);
            return feedback;
        }
    }
}