package com.amazio.user_service.service;

import com.amazio.user_service.dto.ForgotPasswordRequestDto;
import com.amazio.user_service.dto.ResetPasswordRequestDto;
import com.amazio.user_service.entity.PasswordResetToken;
import com.amazio.user_service.entity.User;
import com.amazio.user_service.enums.UserEventType;
import com.amazio.user_service.exception.InvalidResetTokenException;
import com.amazio.user_service.kafka.UserEventProducer;
import com.amazio.user_service.repository.PasswordResetTokenRepo;
import com.amazio.user_service.repository.UserRepo;

import lombok.RequiredArgsConstructor;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;

@Service
@RequiredArgsConstructor
public class PasswordResetService {

    private final UserRepo userRepo;
    private final PasswordResetTokenRepo tokenRepo;
    private final PasswordEncoder passwordEncoder;
    private final UserEventProducer userEventProducer;

    private static final int TOKEN_EXPIRY_MINUTES = 15;

    private final SecureRandom secureRandom = new SecureRandom();

    @Transactional
    public void forgotPassword(ForgotPasswordRequestDto request) {

        User user = userRepo.findByEmail(request.email())
                .orElse(null);

        // Always return a generic response to the frontend.
        if (user == null) {
            return;
        }

        // Invalidate previous reset tokens.
        tokenRepo.deleteByUserId(user.getUserId());

        // Generate secure random token.
        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        String rawToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .userId(user.getUserId())
                .tokenHash(hashToken(rawToken))
                .expiryDate(
                        LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_MINUTES)
                )
                .used(false)
                .createdAt(LocalDateTime.now())
                .build();

//        PasswordResetToken token=new PasswordResetToken(
//                hashToken(rawToken),
//                user.getUserId(),
//                LocalDateTime.now().plusMinutes(TOKEN_EXPIRY_MINUTES),
//                false,
//                LocalDateTime.now()
//        );

        tokenRepo.save(resetToken);

        // Publish reset event to Notification Service.
        userEventProducer.publishUserEvent(
                user.getUserId(),
                user.getFName(),
                user.getLName(),
                user.getEmail(),
                UserEventType.USER_PASSWORD_RESET.toString(),
                rawToken

        );
    }

    @Transactional
    public void resetPassword(ResetPasswordRequestDto request) {

        String tokenHash = hashToken(request.token());

        PasswordResetToken resetToken = tokenRepo
                .findByTokenHash(tokenHash)
                .orElseThrow(() ->
                        new InvalidResetTokenException(
                                "Invalid or expired reset link"
                        )
                );

        if (resetToken.isUsed()
                || resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {

            throw new InvalidResetTokenException(
                    "Invalid or expired reset link"
            );
        }

        User user = userRepo.findById(resetToken.getUserId())
                .orElseThrow(() ->
                        new InvalidResetTokenException(
                                "Invalid or expired reset link"
                        )
                );

        user.setPassword(
                passwordEncoder.encode(request.newPassword())
        );

        userRepo.save(user);

        resetToken.setUsed(true);
        tokenRepo.save(resetToken);
    }

    private String hashToken(String token) {

        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return Base64.getEncoder().encodeToString(hash);

        } catch (Exception e) {
            throw new IllegalStateException(
                    "Unable to hash reset token", e
            );
        }
    }
}