
package com.amazio.notification_service.kafka;

import com.amazio.notification_service.dto.UserEvent;
import com.amazio.notification_service.service.UserNotificationService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserEventConsumer {

    private final UserNotificationService userNotificationService;


    @KafkaListener(
            topics = "user-events",
            groupId = "user-notification-consumer-group",
            properties = {
                    "spring.json.value.default.type=com.amazio.notification_service.dto.UserEvent"
            }
    )
    public void consumeUserEvent(UserEvent event) throws MessagingException {

        log.info("Received user event: {}", event.eventType());

        switch (event.eventType()) {

            case "USER_SIGNUP" ->
                    userNotificationService.handleUserRegistration(event);
            case "USER_UPDATED" ->
                    userNotificationService.handleUserUpdate(event);
            case "USER_LOGIN" ->
                    userNotificationService.handleUserLogin(event);
            case "USER_LOGIN_FAILED" ->
                    userNotificationService.handleUserLoginFailed(event);
            case "USER_PASSWORD_RESET" ->
                    userNotificationService.handlePasswordReset(event);

            default ->
                    log.warn("Unknown user event type: {}", event.eventType());
        }
    }

}