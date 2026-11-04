package com.amazio.notification_service.service;

import com.amazio.notification_service.dto.InventoryResultEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class InventoryNotificationService {
    public void handleInventoryResult(InventoryResultEvent event) {
        log.info("inside handle inventory result :"+event.toString());
    }
}
