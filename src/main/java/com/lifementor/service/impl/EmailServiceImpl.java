// src/main/java/com/lifementor/service/impl/EmailServiceImpl.java
package com.lifementor.service.impl;

import com.lifementor.service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.frontend.url:http://localhost:3000}")
    private String frontendUrl;

    @Value("${app.frontend.reset-password-path:/reset-password}")
    private String resetPasswordPath;

    @Value("${app.application.name:Life Mentor}")
    private String applicationName;

    public EmailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    @Async
    public void sendResetPasswordEmail(String toEmail, String userName, String resetToken) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(applicationName + " - Password Reset Request");

            String resetLink = frontendUrl + resetPasswordPath + "?token=" + resetToken;

            String emailText = String.format(
                    "Dear %s,\n\n" +
                            "You have requested to reset your password for your %s account.\n\n" +
                            "Please click the link below to reset your password:\n" +
                            "%s\n\n" +
                            "This password reset link is valid for 1 hour.\n\n" +
                            "If you did not request a password reset, please ignore this email. " +
                            "Your password will remain unchanged.\n\n" +
                            "For security reasons, never share this link with anyone.\n\n" +
                            "Best regards,\n" +
                            "The %s Team",
                    userName,
                    applicationName,
                    resetLink,
                    applicationName
            );

            message.setText(emailText);

            mailSender.send(message);
            log.info("Password reset email successfully sent to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send password reset email to {}: {}", toEmail, e.getMessage(), e);
            throw new RuntimeException("Failed to send password reset email: " + e.getMessage());
        }
    }

    @Override
    @Async
    public void sendWelcomeEmail(String toEmail, String userName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject("Welcome to " + applicationName + "!");

            String emailText = String.format(
                    "Dear %s,\n\n" +
                            "Welcome to %s! We're thrilled to have you join our community.\n\n" +
                            "Your account has been successfully created and you can now start " +
                            "your journey towards a healthier and more balanced lifestyle.\n\n" +
                            "With %s, you'll receive personalized guidance and support " +
                            "to help you achieve your wellness goals.\n\n" +
                            "If you have any questions or need assistance, feel free to reach out.\n\n" +
                            "Best regards,\n" +
                            "The %s Team",
                    userName,
                    applicationName,
                    applicationName,
                    applicationName
            );

            message.setText(emailText);

            mailSender.send(message);
            log.info("Welcome email successfully sent to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send welcome email to {}: {}", toEmail, e.getMessage(), e);
            // Don't throw exception for welcome email - it's not critical
        }
    }

    @Override
    @Async
    public void sendPasswordChangedEmail(String toEmail, String userName) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(applicationName + " - Password Changed Successfully");

            String emailText = String.format(
                    "Dear %s,\n\n" +
                            "This is to confirm that your %s account password has been successfully changed.\n\n" +
                            "If you did not make this change, please contact our support team immediately.\n\n" +
                            "For security:\n" +
                            "1. Never share your password with anyone\n" +
                            "2. Use a strong, unique password\n" +
                            "3. Enable two-factor authentication if available\n\n" +
                            "Best regards,\n" +
                            "The %s Team",
                    userName,
                    applicationName,
                    applicationName
            );

            message.setText(emailText);

            mailSender.send(message);
            log.info("Password changed notification email sent to: {}", toEmail);

        } catch (Exception e) {
            log.error("Failed to send password changed email to {}: {}", toEmail, e.getMessage(), e);
            // Don't throw exception - password change already happened
        }
    }
}