package com.lifementor.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class WellbeingSummaryResponse {

    private UUID userId;
    private LocalDate summaryDate;
    private int currentStreak;
    private int totalCheckins;
    private double averageMood;
    private String moodTrend;
    private List<WellbeingAlertResponse> activeAlerts;
    private Map<String, Object> categorySummaries;
    private List<UserGoalResponse> activeGoals;
    private RecommendationResponse todaysRecommendation;

    public static class RecommendationResponse {
        private String title;
        private String description;
        private String category;
        private String action;

        public RecommendationResponse(String title, String description, String category, String action) {
            this.title = title;
            this.description = description;
            this.category = category;
            this.action = action;
        }

        // Getters and Setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public String getAction() { return action; }
        public void setAction(String action) { this.action = action; }
    }

    // Constructors
    public WellbeingSummaryResponse() {}

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID userId;
        private LocalDate summaryDate;
        private int currentStreak;
        private int totalCheckins;
        private double averageMood;
        private String moodTrend;
        private List<WellbeingAlertResponse> activeAlerts;
        private Map<String, Object> categorySummaries;
        private List<UserGoalResponse> activeGoals;
        private RecommendationResponse todaysRecommendation;

        public Builder userId(UUID userId) { this.userId = userId; return this; }
        public Builder summaryDate(LocalDate summaryDate) { this.summaryDate = summaryDate; return this; }
        public Builder currentStreak(int currentStreak) { this.currentStreak = currentStreak; return this; }
        public Builder totalCheckins(int totalCheckins) { this.totalCheckins = totalCheckins; return this; }
        public Builder averageMood(double averageMood) { this.averageMood = averageMood; return this; }
        public Builder moodTrend(String moodTrend) { this.moodTrend = moodTrend; return this; }
        public Builder activeAlerts(List<WellbeingAlertResponse> activeAlerts) { this.activeAlerts = activeAlerts; return this; }
        public Builder categorySummaries(Map<String, Object> categorySummaries) { this.categorySummaries = categorySummaries; return this; }
        public Builder activeGoals(List<UserGoalResponse> activeGoals) { this.activeGoals = activeGoals; return this; }
        public Builder todaysRecommendation(RecommendationResponse todaysRecommendation) { this.todaysRecommendation = todaysRecommendation; return this; }

        public WellbeingSummaryResponse build() {
            WellbeingSummaryResponse response = new WellbeingSummaryResponse();
            response.userId = this.userId;
            response.summaryDate = this.summaryDate;
            response.currentStreak = this.currentStreak;
            response.totalCheckins = this.totalCheckins;
            response.averageMood = this.averageMood;
            response.moodTrend = this.moodTrend;
            response.activeAlerts = this.activeAlerts;
            response.categorySummaries = this.categorySummaries;
            response.activeGoals = this.activeGoals;
            response.todaysRecommendation = this.todaysRecommendation;
            return response;
        }
    }

    // Getters and Setters
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public LocalDate getSummaryDate() { return summaryDate; }
    public void setSummaryDate(LocalDate summaryDate) { this.summaryDate = summaryDate; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public int getTotalCheckins() { return totalCheckins; }
    public void setTotalCheckins(int totalCheckins) { this.totalCheckins = totalCheckins; }

    public double getAverageMood() { return averageMood; }
    public void setAverageMood(double averageMood) { this.averageMood = averageMood; }

    public String getMoodTrend() { return moodTrend; }
    public void setMoodTrend(String moodTrend) { this.moodTrend = moodTrend; }

    public List<WellbeingAlertResponse> getActiveAlerts() { return activeAlerts; }
    public void setActiveAlerts(List<WellbeingAlertResponse> activeAlerts) { this.activeAlerts = activeAlerts; }

    public Map<String, Object> getCategorySummaries() { return categorySummaries; }
    public void setCategorySummaries(Map<String, Object> categorySummaries) { this.categorySummaries = categorySummaries; }

    public List<UserGoalResponse> getActiveGoals() { return activeGoals; }
    public void setActiveGoals(List<UserGoalResponse> activeGoals) { this.activeGoals = activeGoals; }

    public RecommendationResponse getTodaysRecommendation() { return todaysRecommendation; }
    public void setTodaysRecommendation(RecommendationResponse todaysRecommendation) { this.todaysRecommendation = todaysRecommendation; }
}