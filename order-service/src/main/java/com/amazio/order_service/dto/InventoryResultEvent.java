package com.amazio.order_service.dto;

import java.util.UUID;

public record InventoryResultEvent(
        UUID orderId,
        String eventType,
        String message
) {}