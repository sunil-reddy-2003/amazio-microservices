package com.amazio.user_service.mapper;

import com.amazio.user_service.dto.UserResponseDto;
import com.amazio.user_service.entity.User;

public class UserResponseMapper {

    public static UserResponseDto toDto(User user) {
        return new UserResponseDto(
                user.getUserId(),
                user.getEmail(),
                user.getFName(),
                user.getLName(),
                user.getMobile()
        );
    }
}