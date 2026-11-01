package com.amazio.order_service.dto;

public record OrderItemsDto(
        Integer  productId,
        Integer quantity
) {
}
