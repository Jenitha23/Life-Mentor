package com.lifementor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "daily_checkin_questions")
public class DailyCheckinQuestion {

    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "uniqueidentifier", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "question", columnDefinition = "NVARCHAR(MAX)", nullable = false)
    private String question;

    @Column(name = "question_type", length = 50)
    private String questionType; // YES_NO, SCALE, TEXT, MULTIPLE_CHOICE

    @Column(name = "category", length = 50)
    private String category; // NUTRITION, EXERCISE, SLEEP, MOOD, PRODUCTIVITY, GENERAL

    @Column(name = "options", columnDefinition = "NVARCHAR(MAX)")
    private String options; // JSON string for options

    @Column(name = "is_active", nullable = false)
    private boolean isActive = true;

    @Column(name = "display_order")
    private Integer displayOrder = 0;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public DailyCheckinQuestion() {
    }

    public DailyCheckinQuestion(String question, String questionType, String category, String options, Integer displayOrder) {
        this.question = question;
        this.questionType = questionType;
        this.category = category;
        this.options = options;
        this.displayOrder = displayOrder;
        this.isActive = true;
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

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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
}