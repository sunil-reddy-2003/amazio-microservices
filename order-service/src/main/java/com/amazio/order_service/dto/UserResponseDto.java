package com.amazio.order_service.dto;


import java.util.UUID;

public record UserResponseDto(
     UUID userId,
     String email,
     String fName,
     String lName,
     long mobile
)
{}
