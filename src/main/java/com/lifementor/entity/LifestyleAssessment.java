package com.lifementor.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "lifestyle_assessment")
public class LifestyleAssessment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", columnDefinition = "uniqueidentifier", nullable = false, updatable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "sleep_time")
    private LocalTime sleepTime;

    @Column(name = "wake_up_time")
    private LocalTime wakeUpTime;

    @Min(value = 1, message = "Meals per day must be at least 1")
    @Column(name = "meals_per_day")
    private Integer mealsPerDay;

    @Enumerated(EnumType.STRING)
    @Column(name = "exercise_frequency", length = 10)
    private ExerciseFrequency exerciseFrequency;

    @DecimalMin(value = "0.0", message = "Study/work hours cannot be negative")
    @Column(name = "study_work_hours", precision = 4, scale = 1)
    private BigDecimal studyWorkHours;

    @DecimalMin(value = "0.0", message = "Screen time hours cannot be negative")
    @Column(name = "screen_time_hours", precision = 4, scale = 1)
    private BigDecimal screenTimeHours;

    @Min(value = 1, message = "Mood level must be at least 1")
    @Max(value = 5, message = "Mood level cannot exceed 5")
    @Column(name = "mood_level")
    private Integer moodLevel;

    @Size(max = 1000, message = "Mental wellbeing note cannot exceed 1000 characters")
    @Column(name = "mental_wellbeing_note", columnDefinition = "NVARCHAR(MAX)")
    private String mentalWellbeingNote;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public LifestyleAssessment() {
    }

    public LifestyleAssessment(User user) {
        this.user = user;
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

    public LocalTime getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(LocalTime sleepTime) {
        this.sleepTime = sleepTime;
    }

    public LocalTime getWakeUpTime() {
        return wakeUpTime;
    }

    public void setWakeUpTime(LocalTime wakeUpTime) {
        this.wakeUpTime = wakeUpTime;
    }

    public Integer getMealsPerDay() {
        return mealsPerDay;
    }

    public void setMealsPerDay(Integer mealsPerDay) {
        this.mealsPerDay = mealsPerDay;
    }

    public ExerciseFrequency getExerciseFrequency() {
        return exerciseFrequency;
    }

    public void setExerciseFrequency(ExerciseFrequency exerciseFrequency) {
        this.exerciseFrequency = exerciseFrequency;
    }

    public BigDecimal getStudyWorkHours() {
        return studyWorkHours;
    }

    public void setStudyWorkHours(BigDecimal studyWorkHours) {
        this.studyWorkHours = studyWorkHours;
    }

    public BigDecimal getScreenTimeHours() {
        return screenTimeHours;
    }

    public void setScreenTimeHours(BigDecimal screenTimeHours) {
        this.screenTimeHours = screenTimeHours;
    }

    public Integer getMoodLevel() {
        return moodLevel;
    }

    public void setMoodLevel(Integer moodLevel) {
        this.moodLevel = moodLevel;
    }

    public String getMentalWellbeingNote() {
        return mentalWellbeingNote;
    }

    public void setMentalWellbeingNote(String mentalWellbeingNote) {
        this.mentalWellbeingNote = mentalWellbeingNote;
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

    public enum ExerciseFrequency {
        NONE,
        LOW,
        MODERATE,
        HIGH
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private User user;
        private LocalTime sleepTime;
        private LocalTime wakeUpTime;
        private Integer mealsPerDay;
        private ExerciseFrequency exerciseFrequency;
        private BigDecimal studyWorkHours;
        private BigDecimal screenTimeHours;
        private Integer moodLevel;
        private String mentalWellbeingNote;

        public Builder user(User user) {
            this.user = user;
            return this;
        }

        public Builder sleepTime(LocalTime sleepTime) {
            this.sleepTime = sleepTime;
            return this;
        }

        public Builder wakeUpTime(LocalTime wakeUpTime) {
            this.wakeUpTime = wakeUpTime;
            return this;
        }

        public Builder mealsPerDay(Integer mealsPerDay) {
            this.mealsPerDay = mealsPerDay;
            return this;
        }

        public Builder exerciseFrequency(ExerciseFrequency exerciseFrequency) {
            this.exerciseFrequency = exerciseFrequency;
            return this;
        }

        public Builder studyWorkHours(BigDecimal studyWorkHours) {
            this.studyWorkHours = studyWorkHours;
            return this;
        }

        public Builder screenTimeHours(BigDecimal screenTimeHours) {
            this.screenTimeHours = screenTimeHours;
            return this;
        }

        public Builder moodLevel(Integer moodLevel) {
            this.moodLevel = moodLevel;
            return this;
        }

        public Builder mentalWellbeingNote(String mentalWellbeingNote) {
            this.mentalWellbeingNote = mentalWellbeingNote;
            return this;
        }

        public LifestyleAssessment build() {
            LifestyleAssessment assessment = new LifestyleAssessment();
            assessment.setUser(this.user);
            assessment.setSleepTime(this.sleepTime);
            assessment.setWakeUpTime(this.wakeUpTime);
            assessment.setMealsPerDay(this.mealsPerDay);
            assessment.setExerciseFrequency(this.exerciseFrequency);
            assessment.setStudyWorkHours(this.studyWorkHours);
            assessment.setScreenTimeHours(this.screenTimeHours);
            assessment.setMoodLevel(this.moodLevel);
            assessment.setMentalWellbeingNote(this.mentalWellbeingNote);
            return assessment;
        }
    }
}