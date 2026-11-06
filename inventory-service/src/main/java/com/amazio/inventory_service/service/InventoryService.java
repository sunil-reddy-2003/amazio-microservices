package com.amazio.inventory_service.service;

import com.amazio.inventory_service.dto.InventoryResultEvent;
import com.amazio.inventory_service.dto.OrderCreatedEvent;
import com.amazio.inventory_service.dto.ProductCreatedEvent;
import com.amazio.inventory_service.entity.Inventory;
import com.amazio.inventory_service.entity.InventoryReservation;
import com.amazio.inventory_service.enums.ReservationStatus;
import com.amazio.inventory_service.exception.InventoryNotFoundException;
import com.amazio.inventory_service.kafka.InventoryEventProducer;
import com.amazio.inventory_service.repository.InventoryRepo;

import com.amazio.inventory_service.repository.InventoryReservationRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryService {

    private final InventoryRepo inventoryRepo;
    private final InventoryEventProducer inventoryEventProducer;
    private final InventoryReservationRepo inventoryReservationRepo;

    @Transactional
    public void createInventory(ProductCreatedEvent event) {

        if (event.productId() == null) {
            throw new IllegalArgumentException("Product ID cannot be null");
        }

        if (inventoryRepo.findByProductId(event.productId()).isPresent()) {
            return;
        }

        Inventory inventory = new Inventory();
        inventory.setProductId(event.productId());
        inventory.setAvailableQuantity((int)(Math.random() * 51) + 50);
        inventory.setReservedQuantity(0);

        inventoryRepo.save(inventory);
    }

    @Transactional
    public Inventory addStock(int productId, int quantity) {

        if (quantity <= 0) {
            throw new IllegalArgumentException(
                    "Quantity must be greater than zero"
            );
        }

        Inventory inventory = inventoryRepo.findByProductIdForUpdate(productId)
                .orElseThrow(() ->
                        new InventoryNotFoundException(
                                "Inventory not found for product: " + productId
                        ));

        inventory.setAvailableQuantity(
                Math.addExact(inventory.getAvailableQuantity(), quantity)
        );

        return inventoryRepo.save(inventory);
    }

    public Inventory getInventory(int productId) {

        return inventoryRepo.findByProductId(productId)
                .orElseThrow(() ->
                        new InventoryNotFoundException(
                                "Inventory not found for product: " + productId
                        ));
    }


    @Transactional
    public void reserveStock(OrderCreatedEvent event) {


        // Ignore duplicate events.
        if (inventoryReservationRepo.findByOrderId(event.orderId()).isPresent()) {
            return;
        }

        Map<Integer, Integer> requestedQuantities = new HashMap<>();

        // Step 1: Validate and aggregate quantities.
        if (event.items() == null || event.items().isEmpty()) {
            reject(event, "Order contains no items");
            return;
        }

        for (OrderCreatedEvent.OrderItemEvent item : event.items()) {

            if (item.productId() == null ||
                    item.quantity() == null ||
                    item.productId() <= 0 ||
                    item.quantity() <= 0) {

                reject(event, "Invalid product ID or quantity");
                return;
            }

            requestedQuantities.merge(
                    item.productId(),
                    item.quantity(),
                    Math::addExact
            );
        }

        // Step 2: Validate stock for every product in ascending product ID order.
        Map<Integer, Inventory> inventoryRecords = new HashMap<>();

        for (Integer productId : requestedQuantities.keySet().stream().sorted().toList()) {

            Integer requestedQuantity = requestedQuantities.get(productId);

            Inventory inventory = inventoryRepo.findByProductIdForUpdate(productId)
                    .orElse(null);

            if (inventory == null) {
                reject(event, "Inventory not found for product: " + productId);
                return;
            }

            if (inventory.getAvailableQuantity() < requestedQuantity) {
                reject(event, "Insufficient stock for product: " + productId);
                return;
            }

            inventoryRecords.put(productId, inventory);
        }

        // Step 3: Reserve stock after every product passes validation.
        for (Map.Entry<Integer, Integer> entry : requestedQuantities.entrySet()) {

            Inventory inventory = inventoryRecords.get(entry.getKey());
            int quantity = entry.getValue();

            inventory.setAvailableQuantity(
                    inventory.getAvailableQuantity() - quantity);

            inventory.setReservedQuantity(
                    inventory.getReservedQuantity() + quantity);

            inventoryRepo.save(inventory);
        }

        // Step 4: Record successful reservation in the same transaction.
        inventoryReservationRepo.save(new InventoryReservation(event.orderId(),ReservationStatus.RESERVED,LocalDateTime.now()));


        publishAfterCommit(new InventoryResultEvent(
                event.orderId(),
                "INVENTORY_RESERVED",
                "Stock reserved successfully"
        ));
    }

    private void reject(OrderCreatedEvent event, String reason) {

        log.warn("Inventory reservation rejected for order {}: {}",
                event.orderId(), reason);

        inventoryReservationRepo.save(
                new InventoryReservation(event.orderId(), ReservationStatus.REJECTED, LocalDateTime.now()));

        publishAfterCommit(new InventoryResultEvent(
                event.orderId(),
                "INVENTORY_REJECTED",
                reason
        ));
    }

    private void publishAfterCommit(InventoryResultEvent resultEvent) {

        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {

                    @Override
                    public void afterCommit() {
                        inventoryEventProducer.publishInventoryResult(resultEvent);
                    }
                }
        );
    }
}