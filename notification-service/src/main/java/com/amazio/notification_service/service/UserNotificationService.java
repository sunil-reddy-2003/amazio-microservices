package com.amazio.notification_service.service;

import com.amazio.notification_service.dto.UserEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserNotificationService {

    private final TemplateEngine templateEngine;
    private final BrevoEmailService brevoEmailService;

    @Value("${password-reset-url}")
    private String passwordResetUrl;

    @Value("${change-password-url}")
    private String changePasswordUrl;

    @Value("${home-url}")
    private String homeUrl;


    // USER LOGIN SUCCESS
    public void handleUserLogin(UserEvent event) {

        log.trace("Inside handle user login");

        Context context = new Context();

        context.setVariable("fName", event.fName());
        context.setVariable("lName", event.lName());
        context.setVariable("email", event.email());
        context.setVariable("timestamp", event.timestamp());
        context.setVariable("changePasswordUrl", changePasswordUrl);

        String htmlContent = templateEngine.process("login-success", context);

        sendNotification(
                event.email(),
                "New Login Detected - Amazio",
                htmlContent,
                "Login success"
        );
    }


    // PASSWORD RESET
    public void handlePasswordReset(UserEvent event) {

        log.trace("Inside handle password reset");

        Context context = new Context();

        context.setVariable("fName", event.fName());
        context.setVariable("lName", event.lName());
        context.setVariable("email", event.email());
        context.setVariable(
                "resetPasswordUrl",
                passwordResetUrl + event.message()
        );

        String htmlContent = templateEngine.process("password-reset", context);

        sendNotification(
                event.email(),
                "Reset Your Password - Amazio",
                htmlContent,
                "Password reset"
        );
    }


    // USER REGISTRATION
    public void handleUserRegistration(UserEvent event) {

        log.trace("Inside handle user registration");

        Context context = new Context();

        context.setVariable("fName", event.fName());
        context.setVariable("lName", event.lName());
        context.setVariable("email", event.email());
        context.setVariable("userId", event.userId());
        context.setVariable("homeUrl", homeUrl);

        String htmlContent = templateEngine.process("signup", context);

        sendNotification(
                event.email(),
                "Welcome to Amazio! Your Account Is Ready 🎉",
                htmlContent,
                "Signup"
        );
    }


    // USER UPDATE
    public void handleUserUpdate(UserEvent event) {

        log.trace("Inside handle user update");

        Context context = new Context();

        context.setVariable("fName", event.fName());
        context.setVariable("lName", event.lName());
        context.setVariable("email", event.email());
        context.setVariable("userId", event.userId());
        context.setVariable("changePasswordUrl", changePasswordUrl);

        String htmlContent = templateEngine.process("user-update", context);

        sendNotification(
                event.email(),
                "Your Amazio Account Details Have Been Updated",
                htmlContent,
                "User update"
        );
    }


    // USER LOGIN FAILED
    public void handleUserLoginFailed(UserEvent event) {

        log.trace("Inside handle user login failed");

        Context context = new Context();

        context.setVariable("fName", event.fName());
        context.setVariable("lName", event.lName());
        context.setVariable("email", event.email());
        context.setVariable("userId", event.userId());
        context.setVariable("changePasswordUrl", changePasswordUrl);

        String htmlContent = templateEngine.process("login-failed", context);

        sendNotification(
                event.email(),
                "Security Alert: Failed Login Attempt on Your Amazio Account",
                htmlContent,
                "Login failed"
        );
    }


    // COMMON EMAIL SENDING METHOD
    private void sendNotification(
            String to,
            String subject,
            String htmlContent,
            String notificationType
    ) {

        try {

            brevoEmailService.sendEmail(to, subject, htmlContent);

            log.info("{} email sent successfully \uD83D\uDCE8\uD83D\uDCE4✅", notificationType);

        } catch (Exception e) {

            log.error("Failed to send {} email \uD83D\uDCE8⛔❌", notificationType,  e);

            throw new RuntimeException(
                    "Failed to send " + notificationType + " email: " + e.getMessage(),
                    e
            );
        }
    }
}