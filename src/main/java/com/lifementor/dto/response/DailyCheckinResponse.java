package com.lifementor.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class DailyCheckinResponse {

    private UUID id;
    private UUID questionId;
    private String question;
    private String questionType;
    private String category;
    private String answer;
    private LocalDate responseDate;
    private String metadata;
    private LocalDateTime createdAt;

    // Constructors
    public DailyCheckinResponse() {}

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private UUID questionId;
        private String question;
        private String questionType;
        private String category;
        private String answer;
        private LocalDate responseDate;
        private String metadata;
        private LocalDateTime createdAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder questionId(UUID questionId) { this.questionId = questionId; return this; }
        public Builder question(String question) { this.question = question; return this; }
        public Builder questionType(String questionType) { this.questionType = questionType; return this; }
        public Builder category(String category) { this.category = category; return this; }
        public Builder answer(String answer) { this.answer = answer; return this; }
        public Builder responseDate(LocalDate responseDate) { this.responseDate = responseDate; return this; }
        public Builder metadata(String metadata) { this.metadata = metadata; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public DailyCheckinResponse build() {
            DailyCheckinResponse response = new DailyCheckinResponse();
            response.id = this.id;
            response.questionId = this.questionId;
            response.question = this.question;
            response.questionType = this.questionType;
            response.category = this.category;
            response.answer = this.answer;
            response.responseDate = this.responseDate;
            response.metadata = this.metadata;
            response.createdAt = this.createdAt;
            return response;
        }
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getQuestionId() { return questionId; }
    public void setQuestionId(UUID questionId) { this.questionId = questionId; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getAnswer() { return answer; }
    public void setAnswer(String answer) { this.answer = answer; }

    public LocalDate getResponseDate() { return responseDate; }
    public void setResponseDate(LocalDate responseDate) { this.responseDate = responseDate; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}