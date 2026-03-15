package com.lifementor.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.UUID;

public class GoalProgressUpdateRequest {

    @NotNull(message = "Goal ID is required")
    private UUID goalId;

    @NotNull(message = "Current value is required")
    @PositiveOrZero(message = "Current value must be zero or positive")
    private BigDecimal currentValue;

    private String notes;

    // Getters and Setters
    public UUID getGoalId() { return goalId; }
    public void setGoalId(UUID goalId) { this.goalId = goalId; }

    public BigDecimal getCurrentValue() { return currentValue; }
    public void setCurrentValue(BigDecimal currentValue) { this.currentValue = currentValue; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}