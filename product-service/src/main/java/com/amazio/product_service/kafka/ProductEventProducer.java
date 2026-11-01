package com.amazio.product_service.kafka;


import com.amazio.product_service.dto.ProductCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class ProductEventProducer {

    private static final String TOPIC = "product-events";

    private final KafkaTemplate<String, ProductCreatedEvent> kafkaTemplate;

    public void publishProductCreated(Integer productId) {

        ProductCreatedEvent event =
                new ProductCreatedEvent("PRODUCT_CREATED", productId);

        kafkaTemplate.send(TOPIC, productId.toString(), event)
                .whenComplete((result, exception) -> {
                    if (exception != null) {
                        log.error(
                                "Failed to publish PRODUCT_CREATED for productId={}",
                                productId,
                                exception
                        );
                    } else {
                        log.info(
                                "Published PRODUCT_CREATED: productId={}, partition={}, offset={}",
                                productId,
                                result.getRecordMetadata().partition(),
                                result.getRecordMetadata().offset()
                        );
                    }
                });
    }
}