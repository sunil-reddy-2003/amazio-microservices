package com.amazio.user_service.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserEvent(
        UUID userId,
        String fName,
        String lName,
        String email,
        String eventType,
        String message,
        LocalDateTime timestamp
) {}