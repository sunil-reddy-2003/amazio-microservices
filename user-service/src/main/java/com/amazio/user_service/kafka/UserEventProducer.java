package com.amazio.user_service.kafka;

import com.amazio.user_service.dto.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserEventProducer {

    private static final String TOPIC = "user-events";

    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    public void publishUserEvent(
            UUID userId,
            String email,
            String eventType,
            String message
    ) {
        UserEvent event = new UserEvent(
                userId,
                email,
                eventType,
                message,
                LocalDateTime.now()
        );

        kafkaTemplate.send(
                TOPIC,
                userId != null ? userId.toString() : email,
                event
        ).whenComplete((result, exception) -> {
            if (exception != null) {
                log.error(
                        "Failed to publish user event: {}",
                        eventType,
                        exception
                );
            } else {
                log.info(
                        "Published user event: {}, partition: {}, offset: {}",
                        eventType,
                        result.getRecordMetadata().partition(),
                        result.getRecordMetadata().offset()
                );
            }
        });
    }
}