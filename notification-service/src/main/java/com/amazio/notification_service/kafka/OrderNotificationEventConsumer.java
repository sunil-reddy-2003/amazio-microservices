package com.amazio.notification_service.kafka;

import com.amazio.notification_service.dto.OrderNotificationEvent;
import com.amazio.notification_service.service.OrderNotificationService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;


@Component
@RequiredArgsConstructor
@Slf4j
public class OrderNotificationEventConsumer {

    private final OrderNotificationService orderNotificationService;


    @KafkaListener(
            topics = "order-notifications",
            groupId = "order-info-group",
            properties = {
                    "spring.json.value.default.type=com.amazio.notification_service.dto.OrderNotificationEvent"
            }
    )
    public void consumeOrderNotificationEvent(OrderNotificationEvent event) throws MessagingException {
        orderNotificationService.handleOrderNotification(event);
    }

}