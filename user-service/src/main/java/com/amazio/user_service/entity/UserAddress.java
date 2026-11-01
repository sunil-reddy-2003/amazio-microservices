package com.amazio.user_service.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserAddress {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true,nullable = false)
    private int addressId;
    @Column( nullable = false)
    private int pincode;
    @Column(nullable = false)
    private String area;
    @Column(nullable = false)
    private String flat;
    @Column(nullable = false)
    private String landmark;
    @Column(nullable = false)
    private String city;
    @Column(nullable = false)
    private String state;
    @Column(nullable = false)
    private String addressType;
    @Column(nullable = false,name = "is_default")
    private boolean defaultAddress;
    @Column(name = "receiverName", nullable = false)
    private String name;
    @Column(name = "receiverMobile", nullable = false)
    private long mobile;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Override
    public String toString() {
        return "UserAddress{" +
                "addressId=" + addressId +
                ", pincode=" + pincode +
                ", area='" + area + '\'' +
                ", flat='" + flat + '\'' +
                ", landmark='" + landmark + '\'' +
                ", city='" + city + '\'' +
                ", state='" + state + '\'' +
                ", addressType='" + addressType + '\'' +
                ", defaultAddress=" + defaultAddress +
                ", name='" + name + '\'' +
                ", mobile=" + mobile +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", userId=" + userId +
                '}';
    }
}

