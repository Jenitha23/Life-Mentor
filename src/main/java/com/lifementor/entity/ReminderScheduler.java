package com.lifementor.entity;

import com.lifementor.dto.response.DailyCheckinQuestionResponse;
import com.lifementor.service.DailyCheckinService;
import com.lifementor.service.EmailService;
import com.lifementor.service.GoalService;
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

    private final DailyCheckinService checkinService;
    private final GoalService goalService;
    private final WellbeingService wellbeingService;
    private final EmailService emailService;
    private final UserRepository userRepository;

    @Value("${app.checkin.reminder.enabled:true}")
    private boolean checkinReminderEnabled;

    @Value("${app.checkin.reminder.question-count:3}")
    private int reminderQuestionCount;

    public ReminderScheduler(DailyCheckinService checkinService,
                             GoalService goalService,
                             WellbeingService wellbeingService,
                             EmailService emailService,
                             UserRepository userRepository) {
        this.checkinService = checkinService;
        this.goalService = goalService;
        this.wellbeingService = wellbeingService;
        this.emailService = emailService;
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
            }
        });
    }

    @Scheduled(cron = "0 0 9 * * MON") // Every Monday at 9 AM
    public void sendWeeklyWellbeingSummary() {
        log.info("Sending weekly wellbeing summaries");
        // Implementation for weekly wellbeing summaries
    }

    @Scheduled(cron = "0 0 12 * * *") // Every day at 12 PM
    public void checkWellbeingAlerts() {
        log.info("Checking wellbeing alerts");
        // Implementation for checking and sending alerts
    }
}
