package com.amazio.notification_service.kafka;

import com.amazio.notification_service.dto.InventoryResultEvent;
import com.amazio.notification_service.dto.OrderCreatedEvent;
import com.amazio.notification_service.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderEventConsumer {

    private final NotificationService notificationService;


    @KafkaListener(
            topics = "order-events",
            groupId = "order-notification-group",
            properties = {
                    "spring.json.value.default.type=com.amazio.notification_service.dto.OrderCreatedEvent"
            }
    )
    public void consumeOrderCreated(OrderCreatedEvent event) {
        notificationService.handleOrderCreated(event);
    }

    @KafkaListener(
            topics = "inventory-events",
            groupId = "notification-inventory-group",
            properties = {
                    "spring.json.value.default.type=com.amazio.notification_service.dto.InventoryResultEvent"
            }
    )
    public void consumeInventoryResultEvent(InventoryResultEvent event){
        notificationService.handleInventoryResult(event);
    }
}