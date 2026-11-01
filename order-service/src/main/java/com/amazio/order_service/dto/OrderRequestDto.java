package com.amazio.order_service.dto;


import com.amazio.order_service.entity.OrderItem;
import java.util.List;
import java.util.UUID;

public record OrderRequestDto(
        UUID userId,
        List<OrderItemsDto> orderItems,
        OrderAddressDto address,
        String paymentMethod
) {}