package com.lifementor.service;

public interface PasswordService {
    String hashPassword(String password);
    boolean verifyPassword(String rawPassword, String hashedPassword);
    boolean validatePasswordStrength(String password);
}