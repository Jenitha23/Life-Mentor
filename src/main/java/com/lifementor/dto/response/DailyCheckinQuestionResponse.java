package com.lifementor.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

public class DailyCheckinQuestionResponse {

    private UUID id;
    private String question;
    private String questionType;
    private String category;
    private String options;
    private Integer displayOrder;
    private LocalDateTime createdAt;

    public DailyCheckinQuestionResponse() {
    }

    public DailyCheckinQuestionResponse(UUID id, String question, String questionType, String category,
                                        String options, Integer displayOrder, LocalDateTime createdAt) {
        this.id = id;
        this.question = question;
        this.questionType = questionType;
        this.category = category;
        this.options = options;
        this.displayOrder = displayOrder;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getOptions() {
        return options;
    }

    public void setOptions(String options) {
        this.options = options;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String question;
        private String questionType;
        private String category;
        private String options;
        private Integer displayOrder;
        private LocalDateTime createdAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder question(String question) {
            this.question = question;
            return this;
        }

        public Builder questionType(String questionType) {
            this.questionType = questionType;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public Builder options(String options) {
            this.options = options;
            return this;
        }

        public Builder displayOrder(Integer displayOrder) {
            this.displayOrder = displayOrder;
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public DailyCheckinQuestionResponse build() {
            return new DailyCheckinQuestionResponse(
                    id, question, questionType, category, options, displayOrder, createdAt
            );
        }
    }
}
