package com.amazio.inventory_service.repository;


import com.amazio.inventory_service.entity.InventoryReservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface InventoryReservationRepo
        extends JpaRepository<InventoryReservation, Long> {

    Optional<InventoryReservation> findByOrderId(UUID orderId);
}