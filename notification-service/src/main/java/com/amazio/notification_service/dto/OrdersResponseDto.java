package com.amazio.notification_service.dto;

import com.amazio.notification_service.enums.OrderStatus;

import java.util.UUID;

public record OrdersResponseDto(
        UUID orderId,
        OrderStatus orderStatus,
        String date,
        double totalPrice,
        int totalQuantity
) {}