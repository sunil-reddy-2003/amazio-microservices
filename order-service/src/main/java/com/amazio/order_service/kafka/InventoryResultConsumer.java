package com.amazio.order_service.kafka;

import com.amazio.order_service.dto.InventoryResultEvent;
import com.amazio.order_service.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryResultConsumer {

    private final OrderService orderService;

    @KafkaListener(
            topics = "inventory-events",
            groupId = "order-inventory-result-group",
            properties = {
                    "spring.json.value.default.type=com.amazio.order_service.dto.InventoryResultEvent"
            }
    )
    public void consumeInventoryResult(InventoryResultEvent event) {

        log.info("Received {} for order {}",
                event.eventType(), event.orderId());

        switch (event.eventType()) {
            case "INVENTORY_RESERVED" ->
                    orderService.confirmOrder(event.orderId());

            case "INVENTORY_REJECTED" ->
                    orderService.rejectOrder(
                            event.orderId(), event.message());

            default ->
                    log.warn("Unknown inventory event type: {}",
                            event.eventType());
        }
    }
}