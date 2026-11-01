package com.amazio.order_service.dto;

public record AddressResponseDto(
        int pincode,
        String area,
        String flat,
        String landmark,
        String city,
        String state
) {
}
