package com.lifementor.dto.response;

import com.lifementor.entity.LifestyleAssessment;
import java.math.BigDecimal;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.util.UUID;

public class LifestyleAssessmentResponse {

    private UUID id;
    private UUID userId;
    private LocalTime sleepTime;
    private LocalTime wakeUpTime;
    private Integer mealsPerDay;
    private LifestyleAssessment.ExerciseFrequency exerciseFrequency;
    private BigDecimal studyWorkHours;
    private BigDecimal screenTimeHours;
    private Integer moodLevel;
    private String mentalWellbeingNote;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Constructors
    public LifestyleAssessmentResponse() {}

    public LifestyleAssessmentResponse(UUID id, UUID userId, LocalTime sleepTime, LocalTime wakeUpTime,
                                       Integer mealsPerDay, LifestyleAssessment.ExerciseFrequency exerciseFrequency,
                                       BigDecimal studyWorkHours, BigDecimal screenTimeHours, Integer moodLevel,
                                       String mentalWellbeingNote, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.userId = userId;
        this.sleepTime = sleepTime;
        this.wakeUpTime = wakeUpTime;
        this.mealsPerDay = mealsPerDay;
        this.exerciseFrequency = exerciseFrequency;
        this.studyWorkHours = studyWorkHours;
        this.screenTimeHours = screenTimeHours;
        this.moodLevel = moodLevel;
        this.mentalWellbeingNote = mentalWellbeingNote;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
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

    public LifestyleAssessment.ExerciseFrequency getExerciseFrequency() {
        return exerciseFrequency;
    }

    public void setExerciseFrequency(LifestyleAssessment.ExerciseFrequency exerciseFrequency) {
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

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private UUID userId;
        private LocalTime sleepTime;
        private LocalTime wakeUpTime;
        private Integer mealsPerDay;
        private LifestyleAssessment.ExerciseFrequency exerciseFrequency;
        private BigDecimal studyWorkHours;
        private BigDecimal screenTimeHours;
        private Integer moodLevel;
        private String mentalWellbeingNote;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;

        public Builder id(UUID id) {
            this.id = id;
            return this;
        }

        public Builder userId(UUID userId) {
            this.userId = userId;
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

        public Builder exerciseFrequency(LifestyleAssessment.ExerciseFrequency exerciseFrequency) {
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

        public Builder createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            this.updatedAt = updatedAt;
            return this;
        }

        public LifestyleAssessmentResponse build() {
            return new LifestyleAssessmentResponse(id, userId, sleepTime, wakeUpTime, mealsPerDay,
                    exerciseFrequency, studyWorkHours, screenTimeHours, moodLevel,
                    mentalWellbeingNote, createdAt, updatedAt);
        }
    }
}