package com.lifementor.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = "email")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "BINARY(16)")
    private UUID id;

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    @Column(nullable = false, unique = true, length = 255)
    private String email;

    @NotBlank(message = "Password is required")
    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "reset_token", length = 255)
    private String resetToken;

    @Column(name = "reset_token_expiry")
    private LocalDateTime resetTokenExpiry;

    @Column(name = "email_verified", nullable = false)
    private boolean emailVerified = false;

    @Column(name = "failed_login_attempts", nullable = false)
    private int failedLoginAttempts = 0;

    @Column(name = "account_locked", nullable = false)
    private boolean accountLocked = false;

    @Column(name = "lock_until")
    private LocalDateTime lockUntil;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "bio", length = 500)
    private String bio;

    @Column(name = "date_of_birth")
    private String dateOfBirth;

    @Column(name = "gender", length = 20)
    private String gender;

    @Column(name = "profile_picture_url", length = 500)
    private String profilePictureUrl;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    // Relationships
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private LifestyleAssessment lifestyleAssessment;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AIChatConversation> chatConversations = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DailyCheckinResponseEntity> checkinResponses = new ArrayList<>(); // FIXED: Changed to DailyCheckinResponseEntity

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserGoal> goals = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<WellbeingAlert> wellbeingAlerts = new ArrayList<>();

    // Constructors
    public User() {}

    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getResetToken() { return resetToken; }
    public void setResetToken(String resetToken) { this.resetToken = resetToken; }

    public LocalDateTime getResetTokenExpiry() { return resetTokenExpiry; }
    public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) { this.resetTokenExpiry = resetTokenExpiry; }

    public boolean isEmailVerified() { return emailVerified; }
    public void setEmailVerified(boolean emailVerified) { this.emailVerified = emailVerified; }

    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(int failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }

    public boolean isAccountLocked() { return accountLocked; }
    public void setAccountLocked(boolean accountLocked) { this.accountLocked = accountLocked; }

    public LocalDateTime getLockUntil() { return lockUntil; }
    public void setLockUntil(LocalDateTime lockUntil) { this.lockUntil = lockUntil; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getBio() { return bio; }
    public void setBio(String bio) { this.bio = bio; }

    public String getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getProfilePictureUrl() { return profilePictureUrl; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }

    public LifestyleAssessment getLifestyleAssessment() { return lifestyleAssessment; }
    public void setLifestyleAssessment(LifestyleAssessment lifestyleAssessment) { this.lifestyleAssessment = lifestyleAssessment; }

    public List<AIChatConversation> getChatConversations() { return chatConversations; }
    public void setChatConversations(List<AIChatConversation> chatConversations) { this.chatConversations = chatConversations; }

    // FIXED: Changed return type to List<DailyCheckinResponseEntity>
    public List<DailyCheckinResponseEntity> getCheckinResponses() { return checkinResponses; }
    public void setCheckinResponses(List<DailyCheckinResponseEntity> checkinResponses) { this.checkinResponses = checkinResponses; }

    public List<UserGoal> getGoals() { return goals; }
    public void setGoals(List<UserGoal> goals) { this.goals = goals; }

    public List<WellbeingAlert> getWellbeingAlerts() { return wellbeingAlerts; }
    public void setWellbeingAlerts(List<WellbeingAlert> wellbeingAlerts) { this.wellbeingAlerts = wellbeingAlerts; }

    // Business logic methods
    public void incrementFailedLoginAttempts() {
        this.failedLoginAttempts++;
        if (this.failedLoginAttempts >= 5) {
            this.accountLocked = true;
            this.lockUntil = LocalDateTime.now().plusMinutes(30);
        }
    }

    public void resetFailedLoginAttempts() {
        this.failedLoginAttempts = 0;
        this.accountLocked = false;
        this.lockUntil = null;
    }

    public boolean isAccountCurrentlyLocked() {
        if (this.lockUntil != null && LocalDateTime.now().isBefore(this.lockUntil)) {
            return true;
        }
        if (this.accountLocked && (this.lockUntil == null || LocalDateTime.now().isAfter(this.lockUntil))) {
            resetFailedLoginAttempts();
        }
        return this.accountLocked;
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private UUID id;
        private String name;
        private String email;
        private String password;
        private String resetToken;
        private LocalDateTime resetTokenExpiry;
        private boolean emailVerified;
        private int failedLoginAttempts;
        private boolean accountLocked;
        private LocalDateTime lockUntil;
        private String phoneNumber;
        private String bio;
        private String dateOfBirth;
        private String gender;
        private String profilePictureUrl;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private LocalDateTime lastLogin;

        public Builder id(UUID id) { this.id = id; return this; }
        public Builder name(String name) { this.name = name; return this; }
        public Builder email(String email) { this.email = email; return this; }
        public Builder password(String password) { this.password = password; return this; }
        public Builder resetToken(String resetToken) { this.resetToken = resetToken; return this; }
        public Builder resetTokenExpiry(LocalDateTime resetTokenExpiry) { this.resetTokenExpiry = resetTokenExpiry; return this; }
        public Builder emailVerified(boolean emailVerified) { this.emailVerified = emailVerified; return this; }
        public Builder failedLoginAttempts(int failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; return this; }
        public Builder accountLocked(boolean accountLocked) { this.accountLocked = accountLocked; return this; }
        public Builder lockUntil(LocalDateTime lockUntil) { this.lockUntil = lockUntil; return this; }
        public Builder phoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; return this; }
        public Builder bio(String bio) { this.bio = bio; return this; }
        public Builder dateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; return this; }
        public Builder gender(String gender) { this.gender = gender; return this; }
        public Builder profilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder updatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; return this; }
        public Builder lastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; return this; }

        public User build() {
            User user = new User();
            user.id = this.id;
            user.name = this.name;
            user.email = this.email;
            user.password = this.password;
            user.resetToken = this.resetToken;
            user.resetTokenExpiry = this.resetTokenExpiry;
            user.emailVerified = this.emailVerified;
            user.failedLoginAttempts = this.failedLoginAttempts;
            user.accountLocked = this.accountLocked;
            user.lockUntil = this.lockUntil;
            user.phoneNumber = this.phoneNumber;
            user.bio = this.bio;
            user.dateOfBirth = this.dateOfBirth;
            user.gender = this.gender;
            user.profilePictureUrl = this.profilePictureUrl;
            user.createdAt = this.createdAt;
            user.updatedAt = this.updatedAt;
            user.lastLogin = this.lastLogin;
            return user;
        }
    }
}