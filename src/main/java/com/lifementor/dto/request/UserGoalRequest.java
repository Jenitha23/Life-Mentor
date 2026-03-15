package com.lifementor.dto.request;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public class UserGoalRequest {

    @NotBlank(message = "Goal type is required")
    @Size(max = 50, message = "Goal type cannot exceed 50 characters")
    private String goalType;

    @NotNull(message = "Target value is required")
    @Positive(message = "Target value must be positive")
    private BigDecimal targetValue;

    @NotNull(message = "Target date is required")
    private LocalDate targetDate;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    // Getters and Setters
    public String getGoalType() { return goalType; }
    public void setGoalType(String goalType) { this.goalType = goalType; }

    public BigDecimal getTargetValue() { return targetValue; }
    public void setTargetValue(BigDecimal targetValue) { this.targetValue = targetValue; }

    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}