package com.amazio.order_service.dto;

public record ProductResponseDto(
        Integer id,
        String name,
        Double price,
        String imageUrl
) {}