package com.amazio.order_service.dto;

import com.amazio.order_service.enums.PaymentStatus;

public record PaymentResponseDto(
        PaymentStatus paymentStatus,
        String paymentMethod,
        String transactionId,
        double amountPaid
) {
}
