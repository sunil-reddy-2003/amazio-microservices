package com.amazio.inventory_service.kafka;

import com.amazio.inventory_service.dto.ProductCreatedEvent;
import com.amazio.inventory_service.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventConsumer {

    @Autowired
    private final InventoryService inventoryService;

    @KafkaListener(
            topics = "product-events",
            groupId = "product-event-group",
            properties = {
                    "spring.json.value.default.type=com.amazio.inventory_service.dto.ProductCreatedEvent"
            }
    )
    public void consumeProductCreated(ProductCreatedEvent event) {

        if (!"PRODUCT_CREATED".equals(event.eventType())) {
            return;
        }

        inventoryService.createInventory(event);
    }

}
