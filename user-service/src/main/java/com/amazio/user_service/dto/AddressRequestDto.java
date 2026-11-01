package com.amazio.user_service.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddressRequestDto {
    private int pincode;
    private String area;
    private String flat;
    private String landmark;
    private String city;
    private String state;
    private String addressType;
    private boolean defaultAddress;
    private String name;
    private long mobile;
}
