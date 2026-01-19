// src/main/java/com/lifementor/service/TokenService.java
package com.lifementor.service;

import com.lifementor.entity.User;

public interface TokenService {
    String generateToken(User user);
    String extractEmail(String token);
    boolean validateToken(String token);
    String generateResetToken();
}