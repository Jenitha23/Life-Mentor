package com.lifementor.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class UserGoalResponse {

    private UUID id;
    private String goalType;
    private BigDecimal targetValue;
    private BigDecimal currentValue;
    private LocalDate targetDate;
    private LocalDate startDate;
    private String status;
    private Integer progressPercentage;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public UserGoalResponse() {}

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String goalType;
        private BigDecimal targetValue;
        private BigDecimal currentValue;
        private LocalDate targetDate;
        private LocalDate startDate;
        private String status;
        private Integer progressPercentage;
        private String description;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder goalType(String goalType) { this.goalType = goalType; return this; }
        public Builder targetValue(BigDecimal targetValue) { this.targetValue = targetValue; return this; }
        public Builder currentValue(BigDecimal currentValue) { this.currentValue = currentValue; return this; }
        public Builder targetDate(LocalDate targetDate) { this.targetDate = targetDate; return this; }
        public Builder startDate(LocalDate startDate) { this.startDate = startDate; return this; }
        public Builder status(String status) { this.status = status; return this; }
        public Builder progressPercentage(Integer progressPercentage) { this.progressPercentage = progressPercentage; return this; }
        public Builder description(String description) { this.description = description; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }

        public UserGoalResponse build() {
            UserGoalResponse response = new UserGoalResponse();
            response.id = this.id;
            response.goalType = this.goalType;
            response.targetValue = this.targetValue;
            response.currentValue = this.currentValue;
            response.targetDate = this.targetDate;
            response.startDate = this.startDate;
            response.status = this.status;
            response.progressPercentage = this.progressPercentage;
            response.description = this.description;
            response.createdAt = this.createdAt;
            response.updatedAt = this.updatedAt;
            return response;
        }
    }

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getGoalType() { return goalType; }
    public void setGoalType(String goalType) { this.goalType = goalType; }

    public BigDecimal getTargetValue() { return targetValue; }
    public void setTargetValue(BigDecimal targetValue) { this.targetValue = targetValue; }

    public BigDecimal getCurrentValue() { return currentValue; }
    public void setCurrentValue(BigDecimal currentValue) { this.currentValue = currentValue; }

    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getProgressPercentage() { return progressPercentage; }
    public void setProgressPercentage(Integer progressPercentage) { this.progressPercentage = progressPercentage; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}