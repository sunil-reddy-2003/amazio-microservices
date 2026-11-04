package com.amazio.user_service.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "password_reset_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String tokenHash;

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private LocalDateTime expiryDate;

    @Column(nullable = false)
    private boolean used;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public PasswordResetToken(String tokenHash, UUID userId, LocalDateTime expiryDate, boolean used, LocalDateTime createdAt) {
        this.tokenHash = tokenHash;
        this.userId = userId;
        this.expiryDate = expiryDate;
        this.used = used;
        this.createdAt = createdAt;
    }
}