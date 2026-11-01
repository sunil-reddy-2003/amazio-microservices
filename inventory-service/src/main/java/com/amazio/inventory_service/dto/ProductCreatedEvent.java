package com.amazio.inventory_service.dto;

public record ProductCreatedEvent(
        String eventType,
        Integer productId
) {}