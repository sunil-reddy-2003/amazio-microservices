package com.amazio.user_service.security.jwt;


import com.amazio.user_service.entity.User;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class JwtService {

    @Getter
    @Value("${jwt.access.expiration.time}")
    private long expirationTime;

    @Value("${jwt.access.secret.password}")
    private String secretPassword;


    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretPassword);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateJwtToken(User user) {

        long now = System.currentTimeMillis();

        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("userId", user.getUserId().toString())
                .claim("role", user.getUserRole().name())
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + expirationTime))
                .signWith(getSigningKey())
                .compact();
    }

    public Claims extractAllClaims(String token) {

        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    public String extractEmail(String token) {
        return extractAllClaims(token).getSubject();
    }

    public String extractRole(String token) {
        return extractAllClaims(token).get("role", String.class);
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(extractAllClaims(token).get("userId", String.class));
    }

    public boolean validateToken(String token) {
        try {
            extractAllClaims(token);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

}