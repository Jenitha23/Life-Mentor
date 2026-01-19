package com.lifementor.service.impl;

import com.lifementor.service.PasswordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.util.regex.Pattern;

@Service
public class PasswordServiceImpl implements PasswordService {

    private static final Logger log = LoggerFactory.getLogger(PasswordServiceImpl.class);

    private final PasswordEncoder passwordEncoder;
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
    );

    public PasswordServiceImpl() {
        this.passwordEncoder = new BCryptPasswordEncoder(12); // Strong encryption
    }

    @Override
    public String hashPassword(String password) {
        return passwordEncoder.encode(password);
    }

    @Override
    public boolean verifyPassword(String rawPassword, String hashedPassword) {
        return passwordEncoder.matches(rawPassword, hashedPassword);
    }

    @Override
    public boolean validatePasswordStrength(String password) {
        boolean isValid = PASSWORD_PATTERN.matcher(password).matches();
        if (!isValid) {
            log.warn("Password does not meet strength requirements");
        }
        return isValid;
    }
}