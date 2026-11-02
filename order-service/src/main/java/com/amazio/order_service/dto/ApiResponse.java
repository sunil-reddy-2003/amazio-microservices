package com.amazio.order_service.dto;

public record ApiResponse(
        boolean success,
        String message,
        ProductResponseDto data
) {}