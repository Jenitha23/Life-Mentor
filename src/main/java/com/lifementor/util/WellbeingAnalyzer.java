package com.lifementor.util;

import com.lifementor.entity.DailyCheckinResponseEntity; // FIXED: Changed from DailyCheckinResponse to DailyCheckinResponseEntity
import com.lifementor.entity.User;
import com.lifementor.entity.WellbeingAlert;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class WellbeingAnalyzer {

    public Map<String, Object> analyzeCategory(String category, List<DailyCheckinResponseEntity> responses) {
        Map<String, Object> insights = new HashMap<>();

        if (responses == null || responses.isEmpty()) {
            return insights;
        }

        switch (category.toUpperCase()) {
            case "MOOD":
                analyzeMood(responses, insights);
                break;
            case "SLEEP":
                analyzeSleep(responses, insights);
                break;
            case "EXERCISE":
                analyzeExercise(responses, insights);
                break;
            case "NUTRITION":
                analyzeNutrition(responses, insights);
                break;
            case "PRODUCTIVITY":
                analyzeProductivity(responses, insights);
                break;
            case "STRESS":
                analyzeStress(responses, insights);
                break;
            case "SOCIAL":
                analyzeSocial(responses, insights);
                break;
            case "SCREEN_TIME":
                analyzeScreenTime(responses, insights);
                break;
        }

        return insights;
    }

    private void analyzeMood(List<DailyCheckinResponseEntity> responses, Map<String, Object> insights) {
        double avgMood = responses.stream()
                .mapToInt(r -> Integer.parseInt(r.getAnswer()))
                .average()
                .orElse(0.0);

        boolean isDeclining = checkMoodDeclining(responses);
        boolean isImproving = checkMoodImproving(responses);

        insights.put("averageMood", avgMood);
        insights.put("moodTrend", isDeclining ? "declining" : (isImproving ? "improving" : "stable"));
        insights.put("recommendation", getMoodRecommendation(avgMood, isDeclining));
    }

    private void analyzeSleep(List<DailyCheckinResponseEntity> responses, Map<String, Object> insights) {
        double avgSleep = responses.stream()
                .mapToDouble(r -> Double.parseDouble(r.getAnswer()))
                .average()
                .orElse(0.0);
        
        insights.put("averageSleep", avgSleep);
        if (avgSleep < 7) {
            insights.put("recommendation", "Try to get at least 7-8 hours of sleep for optimal health");
        } else if (avgSleep > 9) {
            insights.put("recommendation", "You're sleeping a lot. Consider if you're feeling tired during the day");
        } else {
            insights.put("recommendation", "Your sleep duration looks good! Keep maintaining a consistent schedule");
        }
    }

    private void analyzeExercise(List<DailyCheckinResponseEntity> responses, Map<String, Object> insights) {
        long exerciseDays = responses.stream()
                .filter(r -> "yes".equalsIgnoreCase(r.getAnswer()))
                .count();
        
        double exerciseRate = (double) exerciseDays / responses.size() * 100;
        
        insights.put("exerciseDays", exerciseDays);
        insights.put("exerciseRate", exerciseRate);
        
        if (exerciseRate < 30) {
            insights.put("recommendation", "Aim for at least 3 days of exercise per week. Even a 15-minute walk counts!");
        } else if (exerciseRate < 60) {
            insights.put("recommendation", "You're doing great! Try to increase frequency gradually");
        } else {
            insights.put("recommendation", "Excellent exercise habit! Keep up the good work");
        }
    }

    private void analyzeNutrition(List<DailyCheckinResponseEntity> responses, Map<String, Object> insights) {
        // Implementation for nutrition analysis
        long healthyMeals = responses.stream()
                .filter(r -> "yes".equalsIgnoreCase(r.getAnswer()))
                .count();
        
        insights.put("healthyEatingDays", healthyMeals);
        insights.put("recommendation", "Try to include a variety of fruits and vegetables in your meals");
    }

    private void analyzeProductivity(List<DailyCheckinResponseEntity> responses, Map<String, Object> insights) {
        long productiveDays = responses.stream()
                .filter(r -> "yes".equalsIgnoreCase(r.getAnswer()))
                .count();
        
        insights.put("productiveDays", productiveDays);
        insights.put("recommendation", "Break large tasks into smaller, manageable chunks");
    }

    private void analyzeStress(List<DailyCheckinResponseEntity> responses, Map<String, Object> insights) {
        double avgStress = responses.stream()
                .mapToDouble(r -> Double.parseDouble(r.getAnswer()))
                .average()
                .orElse(0.0);
        
        insights.put("averageStress", avgStress);
        if (avgStress > 3.5) {
            insights.put("recommendation", "Consider trying meditation or deep breathing exercises");
        }
    }

    private void analyzeSocial(List<DailyCheckinResponseEntity> responses, Map<String, Object> insights) {
        double avgConnection = responses.stream()
                .mapToDouble(r -> Double.parseDouble(r.getAnswer()))
                .average()
                .orElse(0.0);
        
        insights.put("averageSocialConnection", avgConnection);
        if (avgConnection < 3) {
            insights.put("recommendation", "Try reaching out to a friend or family member today");
        }
    }

    private void analyzeScreenTime(List<DailyCheckinResponseEntity> responses, Map<String, Object> insights) {
        double avgScreenTime = responses.stream()
                .mapToDouble(r -> Double.parseDouble(r.getAnswer()))
                .average()
                .orElse(0.0);
        
        insights.put("averageScreenTime", avgScreenTime);
        if (avgScreenTime > 6) {
            insights.put("recommendation", "Try taking regular breaks from screens. The 20-20-20 rule helps: every 20 minutes, look at something 20 feet away for 20 seconds");
        }
    }

    private boolean checkMoodDeclining(List<DailyCheckinResponseEntity> responses) {
        if (responses.size() < 3) return false;

        List<Integer> moods = responses.stream()
                .map(r -> Integer.parseInt(r.getAnswer()))
                .toList();

        return moods.get(moods.size() - 1) < moods.get(0) &&
               moods.get(moods.size() - 1) < moods.get(moods.size() - 2);
    }

    private boolean checkMoodImproving(List<DailyCheckinResponseEntity> responses) {
        if (responses.size() < 3) return false;

        List<Integer> moods = responses.stream()
                .map(r -> Integer.parseInt(r.getAnswer()))
                .toList();

        return moods.get(moods.size() - 1) > moods.get(0);
    }

    private String getMoodRecommendation(double avgMood, boolean isDeclining) {
        if (avgMood <= 2.0 || isDeclining) {
            return "Consider speaking with a mental health professional or trusted friend about your feelings. You don't have to go through tough times alone.";
        } else if (avgMood <= 3.5) {
            return "Try incorporating small mood-boosting activities into your day, like a short walk, listening to music, or doing something you enjoy.";
        } else {
            return "Your mood is positive! Keep up whatever you're doing and maybe try sharing your strategies with others who might benefit.";
        }
    }

    public List<WellbeingAlert> generateAlerts(User user, List<DailyCheckinResponseEntity> responses) {
        List<WellbeingAlert> alerts = new ArrayList<>();

        if (responses == null || responses.isEmpty()) {
            // Create alert for no check-ins
            WellbeingAlert alert = createAlert(user, "INFO", 
                "You haven't completed any check-ins yet", 
                "Start your wellbeing journey by completing today's check-in");
            alerts.add(alert);
            return alerts;
        }

        // Check for consecutive low mood
        int lowMoodDays = 0;
        int totalMoodDays = 0;
        
        for (DailyCheckinResponseEntity response : responses) {
            if ("MOOD".equals(response.getQuestion().getCategory())) {
                totalMoodDays++;
                try {
                    int mood = Integer.parseInt(response.getAnswer());
                    if (mood <= 2) {
                        lowMoodDays++;
                    } else {
                        lowMoodDays = 0;
                    }
                } catch (NumberFormatException e) {
                    // Skip
                }
            }
        }

        if (lowMoodDays >= 3) {
            WellbeingAlert alert = createAlert(user, "WARNING", 
                "Your mood has been consistently low for " + lowMoodDays + " days", 
                "Consider reaching out to a mental health professional or trusted support person. You can also try talking to friends or family about how you're feeling.");
            alerts.add(alert);
        }

        // Check for sleep issues
        double avgSleep = responses.stream()
                .filter(r -> "SLEEP".equals(r.getQuestion().getCategory()))
                .mapToDouble(r -> Double.parseDouble(r.getAnswer()))
                .average()
                .orElse(0.0);

        if (avgSleep < 6 && avgSleep > 0) {
            WellbeingAlert alert = createAlert(user, "WARNING", 
                "You're averaging less than 6 hours of sleep", 
                "Try to establish a consistent bedtime routine and aim for 7-8 hours of sleep");
            alerts.add(alert);
        }

        // Check for missed check-ins (if they have a history but missed recent days)
        LocalDate today = LocalDate.now();
        boolean checkedInToday = responses.stream()
                .anyMatch(r -> r.getResponseDate().equals(today));

        if (!checkedInToday && !responses.isEmpty()) {
            WellbeingAlert alert = createAlert(user, "INFO", 
                "You haven't completed today's check-in yet", 
                "Take a few minutes to check in with yourself");
            alerts.add(alert);
        }

        // Check for low exercise
        long exerciseDays = responses.stream()
                .filter(r -> "EXERCISE".equals(r.getQuestion().getCategory()))
                .filter(r -> "yes".equalsIgnoreCase(r.getAnswer()))
                .count();

        if (responses.size() >= 7 && exerciseDays < 3) {
            WellbeingAlert alert = createAlert(user, "INFO", 
                "You're exercising less than 3 times per week", 
                "Try to incorporate more physical activity into your routine. Even a short walk helps!");
            alerts.add(alert);
        }

        return alerts;
    }

    private WellbeingAlert createAlert(User user, String level, String message, String suggestedAction) {
        WellbeingAlert alert = new WellbeingAlert();
        alert.setId(UUID.randomUUID());
        alert.setUser(user);
        alert.setLevel(level);
        alert.setMessage(message);
        alert.setSuggestedAction(suggestedAction);
        alert.setCreatedAt(LocalDateTime.now());
        alert.setResolved(false);
        return alert;
    }
}