package com.lifementor.service.impl;

import com.lifementor.entity.User;
import com.lifementor.service.TokenService;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtTokenServiceImpl implements TokenService {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenServiceImpl.class);

    @Value("${app.jwt.secret:your-very-strong-secret-key-with-at-least-256-bits-here}")
    private String jwtSecret;

    @Value("${app.jwt.expiration:86400000}") // 24 hours default
    private Long jwtExpirationMs;

    @Value("${app.reset-token.expiration:3600000}") // 1 hour default
    private Long resetTokenExpirationMs;

    private SecretKey getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);

        if (keyBytes.length < 64) {
            log.warn("JWT secret is only {} bytes ({} bits). Creating 512-bit key by repeating the secret.",
                    keyBytes.length, keyBytes.length * 8);

            byte[] secureKey = new byte[64];
            for (int i = 0; i < 64; i++) {
                secureKey[i] = keyBytes[i % keyBytes.length];
            }

            return Keys.hmacShaKeyFor(secureKey);
        }

        return Keys.hmacShaKeyFor(keyBytes);
    }

    @Override
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .claim("email", user.getEmail())
                .claim("name", user.getName())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    @Override
    public String extractEmail(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return claims.get("email", String.class);
        } catch (JwtException e) {
            log.error("Failed to extract email from token: {}", e.getMessage());
            throw new JwtException("Invalid token");
        }
    }

    @Override
    public UUID extractUserId(String token) {
        try {
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return UUID.fromString(claims.getSubject());
        } catch (JwtException e) {
            log.error("Failed to extract user ID from token: {}", e.getMessage());
            throw new JwtException("Invalid token");
        }
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }

    @Override
    public String generateResetToken() {
        return UUID.randomUUID().toString();
    }
}