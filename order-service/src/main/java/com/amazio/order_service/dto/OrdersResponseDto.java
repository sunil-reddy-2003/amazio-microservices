package com.amazio.order_service.dto;

import com.amazio.order_service.enums.OrderStatus;
import java.util.UUID;

public record OrdersResponseDto(
        UUID orderId,
        OrderStatus orderStatus,
        String date,
        double totalPrice,
        int totalQuantity
) {}