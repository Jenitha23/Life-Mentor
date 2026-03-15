package com.lifementor.service.impl;

import com.lifementor.dto.response.WellbeingAlertResponse;
import com.lifementor.dto.response.WellbeingSummaryResponse;
import com.lifementor.entity.DailyCheckinResponseEntity; 
import com.lifementor.entity.User;
import com.lifementor.entity.WellbeingAlert;
import com.lifementor.exception.ResourceNotFoundException;
import com.lifementor.repository.DailyCheckinResponseRepository;
import com.lifementor.repository.UserRepository;
import com.lifementor.repository.WellbeingAlertRepository;
import com.lifementor.service.DailyCheckinService;
import com.lifementor.service.GoalService;
import com.lifementor.service.WellbeingService;
import com.lifementor.util.WellbeingAnalyzer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class WellbeingServiceImpl implements WellbeingService {

    private static final Logger log = LoggerFactory.getLogger(WellbeingServiceImpl.class);

    private final WellbeingAlertRepository alertRepository;
    private final DailyCheckinResponseRepository checkinRepository;
    private final DailyCheckinService checkinService;
    private final GoalService goalService;
    private final UserRepository userRepository;
    private final WellbeingAnalyzer wellbeingAnalyzer;

    public WellbeingServiceImpl(WellbeingAlertRepository alertRepository,
                                DailyCheckinResponseRepository checkinRepository,
                                DailyCheckinService checkinService,
                                GoalService goalService,
                                UserRepository userRepository,
                                WellbeingAnalyzer wellbeingAnalyzer) {
        this.alertRepository = alertRepository;
        this.checkinRepository = checkinRepository;
        this.checkinService = checkinService;
        this.goalService = goalService;
        this.userRepository = userRepository;
        this.wellbeingAnalyzer = wellbeingAnalyzer;
    }

    @Override
    public WellbeingSummaryResponse generateWellbeingSummary(UUID userId) {
        log.info("Generating wellbeing summary for user: {}", userId);

        User user = getUserById(userId);
        LocalDate today = LocalDate.now();
        LocalDate sevenDaysAgo = today.minusDays(7);

        // Get check-ins for last 7 days
        List<DailyCheckinResponseEntity> recentCheckins = checkinRepository // FIXED: Changed to DailyCheckinResponseEntity
                .findByUserIdAndResponseDateBetweenOrderByResponseDateAscQuestionIdAsc(
                        userId, sevenDaysAgo, today);

        // Calculate metrics
        int currentStreak = checkinService.getCurrentStreak(userId);
        int totalCheckins = (int) checkinRepository.countDistinctResponseDatesByUserId(userId);
        double averageMood = calculateAverageMood(recentCheckins);
        String moodTrend = analyzeMoodTrend(recentCheckins);

        // Get active alerts
        List<WellbeingAlertResponse> activeAlerts = getActiveAlerts(userId);

        // Get category summaries
        Map<String, Object> categorySummaries = generateCategorySummaries(recentCheckins);

        // Get active goals
        var activeGoals = goalService.getActiveGoals(userId);

        // Generate today's recommendation
        var todaysRecommendation = generateTodaysRecommendation(userId, recentCheckins);

        return WellbeingSummaryResponse.builder()
                .userId(userId)
                .summaryDate(today)
                .currentStreak(currentStreak)
                .totalCheckins(totalCheckins)
                .averageMood(averageMood)
                .moodTrend(moodTrend)
                .activeAlerts(activeAlerts)
                .categorySummaries(categorySummaries)
                .activeGoals(activeGoals)
                .todaysRecommendation(todaysRecommendation)
                .build();
    }

    @Override
    public List<WellbeingAlertResponse> getActiveAlerts(UUID userId) {
        log.debug("Fetching active alerts for user: {}", userId);

        return alertRepository.findByUserIdAndResolvedFalseOrderByCreatedAtDesc(userId)
                .stream()
                .map(this::mapAlertToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void resolveAlert(UUID userId, UUID alertId) {
        log.info("Resolving alert: {} for user: {}", alertId, userId);

        WellbeingAlert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert not found"));

        if (!alert.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Alert not found for this user");
        }

        alert.resolve();
        alertRepository.save(alert);
    }

    @Override
    public Map<String, Object> analyzeTrends(UUID userId, LocalDate startDate, LocalDate endDate) {
        log.info("Analyzing trends for user: {} from {} to {}", userId, startDate, endDate);

        List<DailyCheckinResponseEntity> responses = checkinRepository // FIXED: Changed to DailyCheckinResponseEntity
                .findByUserIdAndResponseDateBetweenOrderByResponseDateAscQuestionIdAsc(
                        userId, startDate, endDate);

        Map<String, Object> trends = new HashMap<>();

        // Group by date
        Map<LocalDate, List<DailyCheckinResponseEntity>> byDate = responses.stream() // FIXED: Changed to DailyCheckinResponseEntity
                .collect(Collectors.groupingBy(DailyCheckinResponseEntity::getResponseDate));

        // Calculate daily mood
        Map<LocalDate, Double> dailyMood = new TreeMap<>();
        for (Map.Entry<LocalDate, List<DailyCheckinResponseEntity>> entry : byDate.entrySet()) {
            double avgMood = entry.getValue().stream()
                    .filter(r -> "MOOD".equals(r.getQuestion().getCategory()))
                    .mapToDouble(r -> {
                        try {
                            return Double.parseDouble(r.getAnswer());
                        } catch (NumberFormatException e) {
                            return 0.0;
                        }
                    })
                    .average()
                    .orElse(0.0);
            
            if (avgMood > 0) {
                dailyMood.put(entry.getKey(), avgMood);
            }
        }

        trends.put("dailyMood", dailyMood);
        trends.put("totalCheckins", byDate.size());
        trends.put("completionRate", calculateCompletionRate(responses, startDate, endDate));

        return trends;
    }

    @Override
    public List<Map<String, Object>> generateDailyRecommendations(UUID userId) {
        log.info("Generating daily recommendations for user: {}", userId);

        User user = getUserById(userId);
        LocalDate today = LocalDate.now();
        List<DailyCheckinResponseEntity> todaysCheckins = checkinRepository // FIXED: Changed to DailyCheckinResponseEntity
                .findTodaysResponses(userId, today);

        List<Map<String, Object>> recommendations = new ArrayList<>();

        // Check if user has completed today's check-in
        if (todaysCheckins.isEmpty()) {
            Map<String, Object> recommendation = new HashMap<>();
            recommendation.put("type", "CHECKIN_REMINDER");
            recommendation.put("title", "Complete Your Daily Check-in");
            recommendation.put("description", "Start your day by checking in with yourself");
            recommendation.put("priority", "HIGH");
            recommendations.add(recommendation);
        } else {
            // Analyze today's data and provide personalized recommendations
            for (DailyCheckinResponseEntity response : todaysCheckins) {
                String category = response.getQuestion().getCategory();
                String answer = response.getAnswer();

                if ("MOOD".equals(category) && isLowMood(answer)) {
                    Map<String, Object> rec = new HashMap<>();
                    rec.put("type", "MOOD_BOOST");
                    rec.put("title", "Boost Your Mood");
                    rec.put("description", "Try a short walk or listen to uplifting music");
                    rec.put("priority", "MEDIUM");
                    recommendations.add(rec);
                }

                if ("SLEEP".equals(category) && isLowSleep(answer)) {
                    Map<String, Object> rec = new HashMap<>();
                    rec.put("type", "SLEEP_IMPROVEMENT");
                    rec.put("title", "Improve Your Sleep");
                    rec.put("description", "Try to go to bed 30 minutes earlier tonight");
                    rec.put("priority", "MEDIUM");
                    recommendations.add(rec);
                }
            }
        }

        // Add general recommendations if none specific
        if (recommendations.isEmpty()) {
            Map<String, Object> rec = new HashMap<>();
            rec.put("type", "GENERAL_WELLBEING");
            rec.put("title", "Stay Hydrated");
            rec.put("description", "Remember to drink water throughout the day");
            rec.put("priority", "LOW");
            recommendations.add(rec);
        }

        return recommendations;
    }

    private double calculateAverageMood(List<DailyCheckinResponseEntity> responses) { // FIXED: Changed parameter type
        return responses.stream()
                .filter(r -> "MOOD".equals(r.getQuestion().getCategory()))
                .mapToDouble(r -> {
                    try {
                        return Double.parseDouble(r.getAnswer());
                    } catch (NumberFormatException e) {
                        return 0.0;
                    }
                })
                .average()
                .orElse(0.0);
    }

    private String analyzeMoodTrend(List<DailyCheckinResponseEntity> responses) { // FIXED: Changed parameter type
        List<DailyCheckinResponseEntity> moodResponses = responses.stream()
                .filter(r -> "MOOD".equals(r.getQuestion().getCategory()))
                .collect(Collectors.toList());

        if (moodResponses.size() < 3) {
            return "insufficient_data";
        }

        List<Double> moodValues = moodResponses.stream()
                .mapToDouble(r -> Double.parseDouble(r.getAnswer()))
                .boxed()
                .collect(Collectors.toList());

        double first = moodValues.get(0);
        double last = moodValues.get(moodValues.size() - 1);
        double average = moodValues.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);

        if (last > first && last > average) {
            return "improving";
        } else if (last < first && last < average) {
            return "declining";
        } else {
            return "stable";
        }
    }

    private Map<String, Object> generateCategorySummaries(List<DailyCheckinResponseEntity> responses) { // FIXED: Changed parameter type
        Map<String, Object> summaries = new HashMap<>();

        Map<String, List<DailyCheckinResponseEntity>> byCategory = responses.stream()
                .collect(Collectors.groupingBy(r -> r.getQuestion().getCategory()));

        for (Map.Entry<String, List<DailyCheckinResponseEntity>> entry : byCategory.entrySet()) {
            String category = entry.getKey();
            List<DailyCheckinResponseEntity> categoryResponses = entry.getValue();

            Map<String, Object> categorySummary = new HashMap<>();
            categorySummary.put("count", categoryResponses.size());

            // Calculate average for numeric responses
            double avg = categoryResponses.stream()
                    .filter(r -> isNumeric(r.getAnswer()))
                    .mapToDouble(r -> Double.parseDouble(r.getAnswer()))
                    .average()
                    .orElse(0.0);

            if (avg > 0) {
                categorySummary.put("average", avg);
            }

            summaries.put(category, categorySummary);
        }

        return summaries;
    }

    private WellbeingSummaryResponse.RecommendationResponse generateTodaysRecommendation(
            UUID userId, List<DailyCheckinResponseEntity> recentCheckins) { // FIXED: Changed parameter type

        if (recentCheckins.isEmpty()) {
            return new WellbeingSummaryResponse.RecommendationResponse(
                "Complete Your First Check-in",
                "Start tracking your wellbeing journey today",
                "GENERAL",
                "Take the daily check-in now"
            );
        }

        // Check for low mood
        Optional<Double> latestMood = recentCheckins.stream()
                .filter(r -> "MOOD".equals(r.getQuestion().getCategory()))
                .map(r -> Double.parseDouble(r.getAnswer()))
                .findFirst();

        if (latestMood.isPresent() && latestMood.get() <= 2.0) {
            return new WellbeingSummaryResponse.RecommendationResponse(
                "Take Care of Your Mental Health",
                "Your mood has been low. Consider speaking with a trusted friend or professional.",
                "MENTAL_HEALTH",
                "Reach out to someone today"
            );
        }

        // Check for sleep issues
        Optional<Double> latestSleep = recentCheckins.stream()
                .filter(r -> "SLEEP".equals(r.getQuestion().getCategory()))
                .map(r -> Double.parseDouble(r.getAnswer()))
                .findFirst();

        if (latestSleep.isPresent() && latestSleep.get() < 6.0) {
            return new WellbeingSummaryResponse.RecommendationResponse(
                "Improve Your Sleep",
                "Try to establish a consistent bedtime routine",
                "SLEEP",
                "Go to bed 15 minutes earlier tonight"
            );
        }

        // Default recommendation
        return new WellbeingSummaryResponse.RecommendationResponse(
            "Stay Consistent",
            "Keep up with your daily check-ins to track your progress",
            "GENERAL",
            "Complete today's check-in"
        );
    }

    private double calculateCompletionRate(List<DailyCheckinResponseEntity> responses, // FIXED: Changed parameter type
                                           LocalDate startDate, LocalDate endDate) {
        long totalDays = java.time.temporal.ChronoUnit.DAYS.between(startDate, endDate) + 1;
        long daysWithData = responses.stream()
                .map(DailyCheckinResponseEntity::getResponseDate)
                .distinct()
                .count();

        return totalDays > 0 ? (double) daysWithData / totalDays * 100 : 0.0;
    }

    private boolean isLowMood(String answer) {
        try {
            return Integer.parseInt(answer) <= 2;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isLowSleep(String answer) {
        try {
            return Double.parseDouble(answer) < 6.0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private User getUserById(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private WellbeingAlertResponse mapAlertToResponse(WellbeingAlert alert) {
        return WellbeingAlertResponse.builder()
                .id(alert.getId())
                .level(alert.getLevel())
                .message(alert.getMessage())
                .suggestedAction(alert.getSuggestedAction())
                .resolved(alert.isResolved())
                .createdAt(alert.getCreatedAt())
                .build();
    }
}