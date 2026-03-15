package com.lifementor.entity;

import com.lifementor.service.DailyCheckinService;
import com.lifementor.service.EmailService;
import com.lifementor.service.GoalService;
import com.lifementor.service.WellbeingService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ReminderScheduler {

    private static final Logger log = LoggerFactory.getLogger(ReminderScheduler.class);

    private final DailyCheckinService checkinService;
    private final GoalService goalService;
    private final WellbeingService wellbeingService;
    private final EmailService emailService;

    public ReminderScheduler(DailyCheckinService checkinService,
                             GoalService goalService,
                             WellbeingService wellbeingService,
                             EmailService emailService) {
        this.checkinService = checkinService;
        this.goalService = goalService;
        this.wellbeingService = wellbeingService;
        this.emailService = emailService;
    }

    @Scheduled(cron = "0 0 20 * * *") // Every day at 8 PM
    public void sendCheckinReminders() {
        log.info("Sending daily check-in reminders");
        // Implementation for sending reminders to users who haven't checked in
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