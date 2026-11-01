package com.amazio.user_service.dto;

public record RegisterUserDto (
        String fName,
        String lName,
        String email,
        String password,
        long mobile
){
}
