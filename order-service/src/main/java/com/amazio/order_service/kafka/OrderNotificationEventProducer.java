package com.amazio.order_service.kafka;


import com.amazio.order_service.dto.OrderNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class OrderNotificationEventProducer {

    private static final String TOPIC = "order-notifications";

    private final KafkaTemplate<String, OrderNotificationEvent> kafkaTemplate;

    public void publishNotification(OrderNotificationEvent event) {

        try {
            kafkaTemplate.send(
                    TOPIC,
                    event.orderId().toString(),
                    event
            ).whenComplete((result, exception) -> {

                if (exception != null) {
                    log.error("Notification publishing failed", exception);
                } else {
                    log.info(
                            "Notification published: orderId={}, partition={}, offset={}",
                            event.orderId(),
                            result.getRecordMetadata().partition(),
                            result.getRecordMetadata().offset()
                    );
                }
            });

        } catch (Exception e) {
            log.error("Kafka serialization failed before publishing", e);
        }
    }}