package com.amazio.inventory_service.kafka;

import com.amazio.inventory_service.dto.InventoryResultEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class InventoryEventProducer {

    private static final String TOPIC = "inventory-events";

    private final KafkaTemplate<String, InventoryResultEvent> kafkaTemplate;

    public void publishInventoryResult(InventoryResultEvent event) {

        kafkaTemplate.send(
                TOPIC,
                event.orderId().toString(),
                event
        ).whenComplete((result, exception) -> {

            if (exception != null) {
                log.error(
                        "Failed to publish inventory result for order {}",
                        event.orderId(),
                        exception
                );
            } else {
                log.info(
                        "Published {} for order {}, partition={}, offset={}",
                        event.eventType(),
                        event.orderId(),
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                );
            }
        });
    }
}