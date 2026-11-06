package com.amazio.notification_service.service;

import com.amazio.notification_service.dto.OrderCreatedEvent;
import com.amazio.notification_service.dto.OrderNotificationEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;


@Service
@Slf4j
@RequiredArgsConstructor
public class OrderNotificationService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public void handleOrderNotification(OrderNotificationEvent event) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper =
                new MimeMessageHelper(mimeMessage,true,"UTF-8");

        Context context = new Context();

        context.setVariable("orderId", event.orderId());
        context.setVariable("userId", event.userId());
        context.setVariable("customerName", event.customerName());
        context.setVariable("email", event.email());
        context.setVariable("items", event.items());
        context.setVariable("totalAmount", event.totalAmount());
        context.setVariable("eventType", event.eventType());
        context.setVariable("timestamp", event.timestamp());

        String htmlContent = templateEngine.process("order-confirmation", context);

        try {
            mimeMessageHelper.setTo(event.email());
            mimeMessageHelper.setSubject("Order Confirmed - Amazio | #" + event.orderId());
            mimeMessageHelper.setText(htmlContent, true);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send order confirmation email: "
                    + e.getMessage(), e);
        }
        javaMailSender.send(mimeMessage);
        log.info("Order notification triggered successfully 📨📤✅");
    }

    public void handleOrderCreated(OrderCreatedEvent event) {
        log.info("inside handle order created");
    }
}