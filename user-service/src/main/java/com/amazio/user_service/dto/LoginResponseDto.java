package com.amazio.user_service.dto;


import java.util.UUID;

public record LoginResponseDto(
        String token,
        String tokenType,
        long expiresIn,
        UUID userId,
        String email,
        String fName,
        String lName,
        long mobile,
        String role
) {}