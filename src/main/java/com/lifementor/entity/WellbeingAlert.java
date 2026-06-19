package com.lifementor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "wellbeing_alerts")
public class WellbeingAlert {

    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "uniqueidentifier", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "level", length = 20, nullable = false)
    private String level; // INFO, WARNING, CRITICAL

    @Column(name = "message", columnDefinition = "NVARCHAR(MAX)", nullable = false)
    private String message;

    @Column(name = "suggested_action", columnDefinition = "NVARCHAR(MAX)")
    private String suggestedAction;

    @Column(name = "is_resolved", nullable = false)
    private boolean resolved = false;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "alert_category", length = 50)
    private String alertCategory; // MOOD, SLEEP, EXERCISE, etc.

    @Column(name = "alert_data", columnDefinition = "NVARCHAR(MAX)")
    private String alertData; // Store JSON as string

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public WellbeingAlert() {
    }

    public WellbeingAlert(User user, String level, String message, String suggestedAction) {
        this.user = user;
        this.level = level;
        this.message = message;
        this.suggestedAction = suggestedAction;
        this.resolved = false;
    }

    public WellbeingAlert(User user, String level, String message, String suggestedAction, String alertCategory) {
        this.user = user;
        this.level = level;
        this.message = message;
        this.suggestedAction = suggestedAction;
        this.alertCategory = alertCategory;
        this.resolved = false;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSuggestedAction() {
        return suggestedAction;
    }

    public void setSuggestedAction(String suggestedAction) {
        this.suggestedAction = suggestedAction;
    }

    public boolean isResolved() {
        return resolved;
    }

    public void setResolved(boolean resolved) {
        this.resolved = resolved;

        if (resolved && this.resolvedAt == null) {
            this.resolvedAt = LocalDateTime.now();
        }

        if (!resolved) {
            this.resolvedAt = null;
        }
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }

    public String getAlertCategory() {
        return alertCategory;
    }

    public void setAlertCategory(String alertCategory) {
        this.alertCategory = alertCategory;
    }

    public String getAlertData() {
        return alertData;
    }

    public void setAlertData(String alertData) {
        this.alertData = alertData;
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

    public void resolve() {
        this.resolved = true;
        this.resolvedAt = LocalDateTime.now();
    }

    public void reopen() {
        this.resolved = false;
        this.resolvedAt = null;
    }
}