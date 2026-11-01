package com.amazio.notification_service.service;

import com.amazio.notification_service.dto.InventoryResultEvent;
import com.amazio.notification_service.dto.OrderCreatedEvent;
import com.amazio.notification_service.dto.UserEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("inside handle order created : "+event.toString());
    }

    public void handleInventoryResult(InventoryResultEvent event) {
        log.info("inside handle inventory result :"+event.toString());
    }

    public void handleUser(UserEvent event) {
        log.info("inside handle user :"+event.toString());
    }
}
