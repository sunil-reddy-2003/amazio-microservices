package com.amazio.notification_service.service;

import com.amazio.notification_service.dto.InventoryResultEvent;
import com.amazio.notification_service.dto.OrderCreatedEvent;
import com.amazio.notification_service.dto.UserEvent;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserNotificationService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public void handleUser(UserEvent event) {
        log.info("inside handle user :"+event.toString());
//        UUID userId,
//        String email,
//        String eventType,
//        String message,
//        LocalDateTime timestamp
        // Create an empty email message object (supports HTML emails)
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        // Helper class to easily set subject, receiver, and HTML body
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);

        // Create a context object to store values that will be inserted into the HTML template
        Context context = new Context();

        // Add all airline details to the context → these will replace variables in the HTML file
        context.setVariable("userId", event.userId());
        context.setVariable("email", event.email());
        context.setVariable("eventType", event.eventType());
        context.setVariable("message", event.message());
        context.setVariable("timestamp", event.timestamp());

        // Convert the HTML template (airline-registration-request.html) into final email HTML
        String htmlContent = templateEngine.process("airline-registration-request", context);

        try {
            // Set the receiver of the mail (system admin email)
            mimeMessageHelper.setTo(airlineRegistrationReqDto.getAppAdmin().getEmail());

            // Set the subject of the email
            mimeMessageHelper.setSubject(airlineRegistrationReqDto.getAirline().getAirlineName() + " Registration Request");

            // Set the email body and mark it as HTML content
            mimeMessageHelper.setText(htmlContent, true);

        } catch (Exception e) {
            // Log if anything goes wrong while preparing the email
            log.error(e.getMessage());
        }

// Finally send the email using the configured SMTP server
        javaMailSender.send(mimeMessage);

    }
}
