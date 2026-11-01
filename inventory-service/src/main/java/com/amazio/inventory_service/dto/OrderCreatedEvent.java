package com.amazio.inventory_service.dto;


import java.util.List;
import java.util.UUID;

public record OrderCreatedEvent(
        UUID orderId,
        List<OrderItemEvent> items
) {
    public record OrderItemEvent(
            Integer productId,
            Integer quantity
    ) {}
}