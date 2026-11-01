package com.amazio.inventory_service.entity;


import com.amazio.inventory_service.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory_reservations")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class InventoryReservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "order_id", nullable = false, unique = true)
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public InventoryReservation(UUID orderId, ReservationStatus status, LocalDateTime createdAt) {
        this.orderId = orderId;
        this.status = status;
        this.createdAt = createdAt;
    }
}