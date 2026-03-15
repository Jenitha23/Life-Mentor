package com.lifementor.dto.response;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DailyCheckinAnalyticsResponse {

    private UUID userId;
    private int totalCheckins;
    private int currentStreak;
    private int longestStreak;
    private LocalDate lastCheckinDate;
    private Map<String, CategoryAnalytics> categoryAnalytics;
    private List<MoodTrend> moodTrend;
    private List<CheckinSummary> recentCheckins;

    public static class CategoryAnalytics {
        private String category;
        private int responseCount;
        private Map<String, Object> insights;
        private double averageValue; // for numeric responses

        public CategoryAnalytics(String category, int responseCount, Map<String, Object> insights, double averageValue) {
            this.category = category;
            this.responseCount = responseCount;
            this.insights = insights;
            this.averageValue = averageValue;
        }

        // Getters and Setters
        public String getCategory() { return category; }
        public void setCategory(String category) { this.category = category; }

        public int getResponseCount() { return responseCount; }
        public void setResponseCount(int responseCount) { this.responseCount = responseCount; }

        public Map<String, Object> getInsights() { return insights; }
        public void setInsights(Map<String, Object> insights) { this.insights = insights; }

        public double getAverageValue() { return averageValue; }
        public void setAverageValue(double averageValue) { this.averageValue = averageValue; }
    }

    public static class MoodTrend {
        private LocalDate date;
        private int moodLevel;
        private String note;

        public MoodTrend(LocalDate date, int moodLevel, String note) {
            this.date = date;
            this.moodLevel = moodLevel;
            this.note = note;
        }

        // Getters and Setters
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }

        public int getMoodLevel() { return moodLevel; }
        public void setMoodLevel(int moodLevel) { this.moodLevel = moodLevel; }

        public String getNote() { return note; }
        public void setNote(String note) { this.note = note; }
    }

    public static class CheckinSummary {
        private LocalDate date;
        private int responseCount;
        private boolean completed;

        public CheckinSummary(LocalDate date, int responseCount, boolean completed) {
            this.date = date;
            this.responseCount = responseCount;
            this.completed = completed;
        }

        // Getters and Setters
        public LocalDate getDate() { return date; }
        public void setDate(LocalDate date) { this.date = date; }

        public int getResponseCount() { return responseCount; }
        public void setResponseCount(int responseCount) { this.responseCount = responseCount; }

        public boolean isCompleted() { return completed; }
        public void setCompleted(boolean completed) { this.completed = completed; }
    }

    // Constructors
    public DailyCheckinAnalyticsResponse() {}

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID userId;
        private int totalCheckins;
        private int currentStreak;
        private int longestStreak;
        private LocalDate lastCheckinDate;
        private Map<String, CategoryAnalytics> categoryAnalytics;
        private List<MoodTrend> moodTrend;
        private List<CheckinSummary> recentCheckins;

        public Builder userId(UUID userId) { this.userId = userId; return this; }
        public Builder totalCheckins(int totalCheckins) { this.totalCheckins = totalCheckins; return this; }
        public Builder currentStreak(int currentStreak) { this.currentStreak = currentStreak; return this; }
        public Builder longestStreak(int longestStreak) { this.longestStreak = longestStreak; return this; }
        public Builder lastCheckinDate(LocalDate lastCheckinDate) { this.lastCheckinDate = lastCheckinDate; return this; }
        public Builder categoryAnalytics(Map<String, CategoryAnalytics> categoryAnalytics) { this.categoryAnalytics = categoryAnalytics; return this; }
        public Builder moodTrend(List<MoodTrend> moodTrend) { this.moodTrend = moodTrend; return this; }
        public Builder recentCheckins(List<CheckinSummary> recentCheckins) { this.recentCheckins = recentCheckins; return this; }

        public DailyCheckinAnalyticsResponse build() {
            DailyCheckinAnalyticsResponse response = new DailyCheckinAnalyticsResponse();
            response.userId = this.userId;
            response.totalCheckins = this.totalCheckins;
            response.currentStreak = this.currentStreak;
            response.longestStreak = this.longestStreak;
            response.lastCheckinDate = this.lastCheckinDate;
            response.categoryAnalytics = this.categoryAnalytics;
            response.moodTrend = this.moodTrend;
            response.recentCheckins = this.recentCheckins;
            return response;
        }
    }

    // Getters and Setters
    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public int getTotalCheckins() { return totalCheckins; }
    public void setTotalCheckins(int totalCheckins) { this.totalCheckins = totalCheckins; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int currentStreak) { this.currentStreak = currentStreak; }

    public int getLongestStreak() { return longestStreak; }
    public void setLongestStreak(int longestStreak) { this.longestStreak = longestStreak; }

    public LocalDate getLastCheckinDate() { return lastCheckinDate; }
    public void setLastCheckinDate(LocalDate lastCheckinDate) { this.lastCheckinDate = lastCheckinDate; }

    public Map<String, CategoryAnalytics> getCategoryAnalytics() { return categoryAnalytics; }
    public void setCategoryAnalytics(Map<String, CategoryAnalytics> categoryAnalytics) { this.categoryAnalytics = categoryAnalytics; }

    public List<MoodTrend> getMoodTrend() { return moodTrend; }
    public void setMoodTrend(List<MoodTrend> moodTrend) { this.moodTrend = moodTrend; }

    public List<CheckinSummary> getRecentCheckins() { return recentCheckins; }
    public void setRecentCheckins(List<CheckinSummary> recentCheckins) { this.recentCheckins = recentCheckins; }
}