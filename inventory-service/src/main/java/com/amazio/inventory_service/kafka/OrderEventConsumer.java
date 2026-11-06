package com.amazio.inventory_service.kafka;

import com.amazio.inventory_service.dto.OrderCreatedEvent;
import com.amazio.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final InventoryService inventoryService;

    @KafkaListener(
            topics = "order-events",
            groupId = "order-event-group",
            properties = {
                    "spring.json.value.default.type=com.amazio.inventory_service.dto.OrderCreatedEvent"
            }
    )
    public void consumeOrderCreated(OrderCreatedEvent event) {
        inventoryService.reserveStock(event);
    }
}