package com.lifementor.entity;

import com.lifementor.dto.response.DailyCheckinQuestionResponse;
import com.lifementor.dto.response.WellbeingAlertResponse;
import com.lifementor.dto.response.WellbeingSummaryResponse;
import com.lifementor.service.NotificationService;
import com.lifementor.service.DailyCheckinService;
import com.lifementor.service.EmailService;
import com.lifementor.service.WellbeingService;
import com.lifementor.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReminderScheduler.class);
    private static final String CHECKIN_ACTION_URL = "/daily-checkin";
    private static final String SUMMARY_ACTION_URL = "/wellbeing";

    private final DailyCheckinService checkinService;
    private final WellbeingService wellbeingService;
    private final EmailService emailService;
    private final NotificationService notificationService;
    private final UserRepository userRepository;

    @Value("${app.checkin.reminder.enabled:true}")
    private boolean checkinReminderEnabled;

    @Value("${app.checkin.reminder.question-count:3}")
    private int reminderQuestionCount;

    public ReminderScheduler(DailyCheckinService checkinService,
                             WellbeingService wellbeingService,
                             EmailService emailService,
                             NotificationService notificationService,
                             UserRepository userRepository) {
        this.checkinService = checkinService;
        this.wellbeingService = wellbeingService;
        this.emailService = emailService;
        this.notificationService = notificationService;
        this.userRepository = userRepository;
    }

    @Scheduled(cron = "0 0 20 * * *") // Every day at 8 PM
    public void sendCheckinReminders() {
        log.info("Sending daily check-in reminders");
        if (!checkinReminderEnabled) {
            log.info("Daily check-in reminders are disabled");
            return;
        }

        List<DailyCheckinQuestionResponse> questions = checkinService.getActiveQuestions();
        if (questions.isEmpty()) {
            log.warn("No active daily check-in questions found. Skipping reminder emails.");
            return;
        }

        List<String> questionTexts = questions.stream()
                .limit(Math.max(reminderQuestionCount, 1))
                .map(DailyCheckinQuestionResponse::getQuestion)
                .toList();

        userRepository.findAllUnlockedUsers().forEach(user -> {
            if (!checkinService.hasCompletedTodayCheckin(user.getId())) {
                emailService.sendDailyCheckinReminderEmail(user.getEmail(), user.getName(), questionTexts);
                notificationService.createNotification(
                        user,
                        "CHECKIN_REMINDER",
                        "Complete your daily check-in",
                        "Your daily wellbeing check-in is waiting for you.",
                        CHECKIN_ACTION_URL
                );
            }
        });
    }

    @Scheduled(cron = "0 0 9 * * MON") // Every Monday at 9 AM
    public void sendWeeklyWellbeingSummary() {
        log.info("Sending weekly wellbeing summaries");

        userRepository.findAllUnlockedUsers().forEach(user -> {
            try {
                WellbeingSummaryResponse summary = wellbeingService.generateWellbeingSummary(user.getId());
                emailService.sendWeeklyWellbeingSummaryEmail(user.getEmail(), user.getName(), summary);
                notificationService.createNotification(
                        user,
                        "WELLBEING_SUMMARY",
                        "Your weekly wellbeing summary is ready",
                        buildWeeklySummaryMessage(summary),
                        SUMMARY_ACTION_URL
                );
            } catch (Exception e) {
                log.error("Failed to send weekly wellbeing summary for user {}: {}", user.getId(), e.getMessage(), e);
            }
        });
    }

    @Scheduled(cron = "0 0 12 * * *") // Every day at 12 PM
    public void checkWellbeingAlerts() {
        log.info("Checking wellbeing alerts");
        userRepository.findAllUnlockedUsers().forEach(user -> {
            try {
                List<WellbeingAlertResponse> newAlerts = checkinService.checkWellbeingAlerts(user.getId());
                if (!newAlerts.isEmpty()) {
                    emailService.sendWellbeingAlertEmail(user.getEmail(), user.getName(), newAlerts);
                }
            } catch (Exception e) {
                log.error("Failed to process wellbeing alerts for user {}: {}", user.getId(), e.getMessage(), e);
            }
        });
    }

    private String buildWeeklySummaryMessage(WellbeingSummaryResponse summary) {
        int activeAlertCount = summary.getActiveAlerts() == null ? 0 : summary.getActiveAlerts().size();
        return String.format(
                "Streak: %d day(s), mood trend: %s, active alerts: %d.",
                summary.getCurrentStreak(),
                summary.getMoodTrend(),
                activeAlertCount
        );
    }
}
