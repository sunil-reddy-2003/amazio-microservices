package com.amazio.notification_service.service;

import com.amazio.notification_service.dto.OrderCreatedEvent;
import com.amazio.notification_service.dto.OrderNotificationEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderNotificationService {

    private final TemplateEngine templateEngine;
    private final BrevoEmailService brevoEmailService;

    public void handleOrderNotification(OrderNotificationEvent event) {

        log.trace("Inside handle order notification");

        Context context = new Context();

        context.setVariable("orderId", event.orderId());
        context.setVariable("userId", event.userId());
        context.setVariable("customerName", event.customerName());
        context.setVariable("email", event.email());
        context.setVariable("items", event.items());
        context.setVariable("totalAmount", event.totalAmount());
        context.setVariable("eventType", event.eventType());
        context.setVariable("timestamp", event.timestamp());

        String htmlContent = templateEngine.process(
                "order-confirmation",
                context
        );

        try {
            brevoEmailService.sendEmail(
                    event.email(),
                    "Order Confirmed - Amazio | #" + event.orderId(),
                    htmlContent
            );
            log.info("Order confirmation email sent successfully to {}", event.email());
        } catch (Exception e) {
            log.error("Failed to send order confirmation email to {}", event.email(), e);
            throw new RuntimeException("Failed to send order confirmation email: " + e.getMessage(), e);
        }
    }

    public void handleOrderCreated(OrderCreatedEvent event) {

        log.info("Inside handle order created");
    }
}