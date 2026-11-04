package com.amazio.user_service.dto;

public record ResetPasswordRequestDto(
        String token,
        String newPassword
) {}