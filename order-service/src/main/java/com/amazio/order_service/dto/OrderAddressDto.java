package com.amazio.order_service.dto;

public record OrderAddressDto(
        Integer pincode,
        String area,
        String flat,
        String landmark,
        String city,
        String state
) {}