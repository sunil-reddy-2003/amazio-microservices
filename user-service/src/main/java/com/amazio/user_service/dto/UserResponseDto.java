package com.amazio.user_service.dto;


import java.util.UUID;

public record UserResponseDto (
     UUID userId,
     String email,
     String fname,
     String lname,
     long mobile
)
{}
