package com.lifementor.dto.request;

import com.lifementor.entity.LifestyleAssessment;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.LocalTime;

public class LifestyleAssessmentRequest {

    @NotNull(message = "Sleep time is required")
    private LocalTime sleepTime;

    @NotNull(message = "Wake up time is required")
    private LocalTime wakeUpTime;

    @NotNull(message = "Meals per day is required")
    @Min(value = 1, message = "Meals per day must be at least 1")
    private Integer mealsPerDay;

    @NotNull(message = "Exercise frequency is required")
    private LifestyleAssessment.ExerciseFrequency exerciseFrequency;

    @NotNull(message = "Study/work hours is required")
    @DecimalMin(value = "0.0", message = "Study/work hours cannot be negative")
    @DecimalMax(value = "24.0", message = "Study/work hours cannot exceed 24")
    private BigDecimal studyWorkHours;

    @NotNull(message = "Screen time hours is required")
    @DecimalMin(value = "0.0", message = "Screen time hours cannot be negative")
    @DecimalMax(value = "24.0", message = "Screen time hours cannot exceed 24")
    private BigDecimal screenTimeHours;

    @NotNull(message = "Mood level is required")
    @Min(value = 1, message = "Mood level must be at least 1")
    @Max(value = 5, message = "Mood level cannot exceed 5")
    private Integer moodLevel;

    @Size(max = 1000, message = "Mental wellbeing note cannot exceed 1000 characters")
    private String mentalWellbeingNote;

    // Getters and Setters
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
}