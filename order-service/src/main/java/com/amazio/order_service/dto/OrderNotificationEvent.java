package com.amazio.order_service.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record OrderNotificationEvent(
        UUID orderId,
        UUID userId,
        String email,
        String customerName,
        List<OrderItemDetails> items,
        Double totalAmount,
        String eventType,
        LocalDateTime timestamp
) {
    public record OrderItemDetails(
            Integer productId,
            String productName,
            String imageUrl,
            Integer quantity,
            Double price
    ) {}
}