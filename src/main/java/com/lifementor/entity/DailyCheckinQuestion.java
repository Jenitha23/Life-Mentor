package com.lifementor.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "daily_checkin_questions")
public class DailyCheckinQuestion {

    @Id
    @UuidGenerator
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @Column(name = "question", columnDefinition = "LONGTEXT", nullable = false)
    private String question;

    @Column(name = "question_type", length = 50)
    private String questionType; // YES_NO, SCALE, TEXT, MULTIPLE_CHOICE

    @Column(length = 50)
    private String category; // NUTRITION, EXERCISE, SLEEP, MOOD, PRODUCTIVITY, GENERAL

    @Column(name = "options", columnDefinition = "LONGTEXT")
    private String options; // JSON string for options

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    // Constructors
    public DailyCheckinQuestion() {}

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getQuestion() { return question; }
    public void setQuestion(String question) { this.question = question; }

    public String getQuestionType() { return questionType; }
    public void setQuestionType(String questionType) { this.questionType = questionType; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }

    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}