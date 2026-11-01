package com.amazio.order_service.dto;

import com.amazio.order_service.enums.OrderStatus;

import java.util.List;

public record OrderInfoResponseDto(
        OrderStatus orderStatus,
        String date,
        double totalPrice,
        int totalQuantity,
        List<OrderItemResponseDto>orderItems,
        AddressResponseDto address,
        PaymentResponseDto payment
) {
}
