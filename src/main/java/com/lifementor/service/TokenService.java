package com.lifementor.service;

import com.lifementor.entity.User;

import java.util.UUID;

public interface TokenService {
    String generateToken(User user);
    String extractEmail(String token);
    UUID extractUserId(String token); // ADD THIS METHOD
    boolean validateToken(String token);
    String generateResetToken();
}