package com.lifementor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
        name = "user_checkin_responses",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uq_user_question_response_date",
                        columnNames = {"user_id", "question_id", "response_date"}
                )
        }
)
public class DailyCheckinResponseEntity {

    @Id
    @UuidGenerator
    @Column(name = "id", columnDefinition = "uniqueidentifier", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "question_id", nullable = false)
    private DailyCheckinQuestion question;

    @Column(name = "answer", columnDefinition = "NVARCHAR(MAX)", nullable = false)
    private String answer;

    @Column(name = "response_date", nullable = false)
    private LocalDate responseDate;

    @Column(name = "metadata", columnDefinition = "NVARCHAR(MAX)")
    private String metadata;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public DailyCheckinResponseEntity() {
    }

    public DailyCheckinResponseEntity(User user, DailyCheckinQuestion question, String answer, LocalDate responseDate) {
        this.user = user;
        this.question = question;
        this.answer = answer;
        this.responseDate = responseDate;
    }

    public DailyCheckinResponseEntity(User user, DailyCheckinQuestion question, String answer, LocalDate responseDate, String metadata) {
        this.user = user;
        this.question = question;
        this.answer = answer;
        this.responseDate = responseDate;
        this.metadata = metadata;
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

    public DailyCheckinQuestion getQuestion() {
        return question;
    }

    public void setQuestion(DailyCheckinQuestion question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public LocalDate getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(LocalDate responseDate) {
        this.responseDate = responseDate;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}