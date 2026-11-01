package com.amazio.user_service.dto;

public record LoginRequestDto (
    String email,
    String password
){}
