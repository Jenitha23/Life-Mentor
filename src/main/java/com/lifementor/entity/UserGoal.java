package com.lifementor.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_goals")
public class UserGoal {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "goal_type", length = 50, nullable = false)
    private String goalType; // SLEEP_IMPROVEMENT, EXERCISE_INCREASE, MOOD_IMPROVEMENT, etc.

    @Column(name = "target_value", precision = 10, scale = 2)
    private BigDecimal targetValue;

    @Column(name = "current_value", precision = 10, scale = 2)
    private BigDecimal currentValue;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "status", length = 20)
    private String status; // ACTIVE, COMPLETED, ABANDONED

    @Column(name = "progress_percentage")
    private Integer progressPercentage;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Constructors
    public UserGoal() {}

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

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

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}