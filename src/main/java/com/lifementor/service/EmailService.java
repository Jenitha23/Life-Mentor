package com.lifementor.service;

public interface EmailService {
    void sendResetPasswordEmail(String toEmail, String userName, String resetToken);
    void sendWelcomeEmail(String toEmail, String userName);
    void sendPasswordChangedEmail(String toEmail, String userName);
    void sendDailyCheckinReminderEmail(String toEmail, String userName, java.util.List<String> questions);
}
