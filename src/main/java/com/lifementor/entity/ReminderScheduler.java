package com.lifementor.entity;

import com.lifementor.dto.response.DailyCheckinQuestionResponse;
import com.lifementor.dto.response.WellbeingAlertResponse;
import com.lifementor.dto.response.WellbeingSummaryResponse;
import com.lifementor.entity.User;
import com.lifementor.repository.UserRepository;
import com.lifementor.service.DailyCheckinService;
import com.lifementor.service.EmailService;
import com.lifementor.service.NotificationService;
import com.lifementor.service.WellbeingService;
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

    /**
     * Runs every day at 8 PM.
     * Sends reminder emails and creates in-app notifications
     * for users who have not completed today's check-in.
     */
    @Scheduled(cron = "0 0 20 * * *")
    public void sendCheckinReminders() {
        log.info("Starting daily check-in reminder scheduler");

        if (!checkinReminderEnabled) {
            log.info("Daily check-in reminders are disabled");
            return;
        }

        List<DailyCheckinQuestionResponse> questions = checkinService.getActiveQuestions();

        if (questions == null || questions.isEmpty()) {
            log.warn("No active daily check-in questions found. Skipping reminder emails.");
            return;
        }

        int questionLimit = Math.max(reminderQuestionCount, 1);

        List<String> questionTexts = questions.stream()
                .limit(questionLimit)
                .map(DailyCheckinQuestionResponse::getQuestion)
                .toList();

        List<User> users = userRepository.findAllUnlockedUsers();

        for (User user : users) {
            try {
                boolean completedToday = checkinService.hasCompletedTodayCheckin(user.getId());

                if (!completedToday) {
                    emailService.sendDailyCheckinReminderEmail(
                            user.getEmail(),
                            user.getName(),
                            questionTexts
                    );

                    notificationService.createNotification(
                            user,
                            "CHECKIN_REMINDER",
                            "Complete your daily check-in",
                            "Your daily wellbeing check-in is waiting for you.",
                            CHECKIN_ACTION_URL
                    );

                    log.info("Daily check-in reminder sent to user {}", user.getId());
                }

            } catch (Exception e) {
                log.error(
                        "Failed to send daily check-in reminder for user {}: {}",
                        user.getId(),
                        e.getMessage(),
                        e
                );
            }
        }

        log.info("Daily check-in reminder scheduler completed");
    }

    /**
     * Runs every Monday at 9 AM.
     * Sends weekly wellbeing summary emails and creates notifications.
     */
    @Scheduled(cron = "0 0 9 * * MON")
    public void sendWeeklyWellbeingSummary() {
        log.info("Starting weekly wellbeing summary scheduler");

        List<User> users = userRepository.findAllUnlockedUsers();

        for (User user : users) {
            try {
                WellbeingSummaryResponse summary = wellbeingService.generateWellbeingSummary(user.getId());

                emailService.sendWeeklyWellbeingSummaryEmail(
                        user.getEmail(),
                        user.getName(),
                        summary
                );

                notificationService.createNotification(
                        user,
                        "WELLBEING_SUMMARY",
                        "Your weekly wellbeing summary is ready",
                        buildWeeklySummaryMessage(summary),
                        SUMMARY_ACTION_URL
                );

                log.info("Weekly wellbeing summary sent to user {}", user.getId());

            } catch (Exception e) {
                log.error(
                        "Failed to send weekly wellbeing summary for user {}: {}",
                        user.getId(),
                        e.getMessage(),
                        e
                );
            }
        }

        log.info("Weekly wellbeing summary scheduler completed");
    }

    /**
     * Runs every day at 12 PM.
     * Checks wellbeing alerts and sends alert emails if needed.
     */
    @Scheduled(cron = "0 0 12 * * *")
    public void checkWellbeingAlerts() {
        log.info("Starting wellbeing alert scheduler");

        List<User> users = userRepository.findAllUnlockedUsers();

        for (User user : users) {
            try {
                List<WellbeingAlertResponse> newAlerts = checkinService.checkWellbeingAlerts(user.getId());

                if (newAlerts != null && !newAlerts.isEmpty()) {
                    emailService.sendWellbeingAlertEmail(
                            user.getEmail(),
                            user.getName(),
                            newAlerts
                    );

                    log.info("Wellbeing alert email sent to user {}", user.getId());
                }

            } catch (Exception e) {
                log.error(
                        "Failed to process wellbeing alerts for user {}: {}",
                        user.getId(),
                        e.getMessage(),
                        e
                );
            }
        }

        log.info("Wellbeing alert scheduler completed");
    }

    private String buildWeeklySummaryMessage(WellbeingSummaryResponse summary) {
        int activeAlertCount = summary.getActiveAlerts() == null
                ? 0
                : summary.getActiveAlerts().size();

        return String.format(
                "Streak: %d day(s), mood trend: %s, active alerts: %d.",
                summary.getCurrentStreak(),
                summary.getMoodTrend(),
                activeAlertCount
        );
    }
}