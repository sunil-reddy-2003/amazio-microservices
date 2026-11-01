package com.amazio.order_service.dto;


public record OrderItemResponseDto(
        int productId,
        int quantity,
        String imageUrl,
        double price
) {}