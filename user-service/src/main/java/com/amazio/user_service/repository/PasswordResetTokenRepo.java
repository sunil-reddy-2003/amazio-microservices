package com.amazio.user_service.repository;

import com.amazio.user_service.entity.PasswordResetToken;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PasswordResetTokenRepo
        extends JpaRepository<PasswordResetToken, UUID> {

    Optional<PasswordResetToken> findByTokenHash(String tokenHash);

    void deleteByUserId(UUID userId);
}