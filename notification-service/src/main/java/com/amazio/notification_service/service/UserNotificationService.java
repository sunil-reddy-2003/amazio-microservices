package com.amazio.notification_service.service;

import com.amazio.notification_service.dto.UserEvent;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;



@Service
@Slf4j
@RequiredArgsConstructor
public class UserNotificationService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    @Value("${password-reset-url}")
    private String passwordResetUrl;

    @Value("${change-password-url}")
    private String changePasswordUrl;

    @Value("${home-url}")
    private String homeUrl;
    @Value("${spring.mail.email}")
    private String brevoEmail;

    public void handleUserLogin(UserEvent event) throws MessagingException {
        log.trace("inside handle user login");
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true,"UTF-8");
        Context context = new Context();

        context.setVariable("fName", event.fName());
        context.setVariable("lName",event.lName());
        context.setVariable("email",event.email());
        context.setVariable("timestamp",event.timestamp());
        context.setVariable("changePasswordUrl",changePasswordUrl);

        String htmlContent = templateEngine.process("login-success", context);

        try {
            mimeMessageHelper.setFrom(brevoEmail);
            mimeMessageHelper.setTo(event.email());
            mimeMessageHelper.setSubject("New Login Detected - Amazio");
            mimeMessageHelper.setText(htmlContent, true);
            log.info("login success email triggered successfully 📨📤✅");

        } catch (Exception e) {
            log.error("failed to send login success email 📨⛔❌",e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
        javaMailSender.send(mimeMessage);
    }

    public void handlePasswordReset(UserEvent event) throws MessagingException {
        log.trace("inside handle password reset");
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true,"UTF-8");
        Context context = new Context();

        context.setVariable("fName", event.fName());
        context.setVariable("lName",event.lName());
        context.setVariable("email",event.email());
        context.setVariable("resetPasswordUrl", passwordResetUrl + event.message()
        );
        String htmlContent = templateEngine.process("password-reset", context);

        try {
            mimeMessageHelper.setFrom(brevoEmail);
            mimeMessageHelper.setTo(event.email());
            mimeMessageHelper.setSubject("Reset Your Password - Amazio");
            mimeMessageHelper.setText(htmlContent, true);
            log.info("password reset email triggered successfully 📨📤✅");

        } catch (Exception e) {
            log.error("failed to send password reset email \uD83D\uDCE8⛔❌",e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
        javaMailSender.send(mimeMessage);
    }

    public void handleUserRegistration(UserEvent event) throws MessagingException {
        log.trace("inside handle user registration");
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true,"UTF-8");
        Context context = new Context();

        context.setVariable("fName", event.fName());
        context.setVariable("lName",event.lName());
        context.setVariable("email",event.email());
        context.setVariable("userId",event.userId());
        context.setVariable("homeUrl",homeUrl);


        String htmlContent = templateEngine.process("signup", context);

        try {
            mimeMessageHelper.setFrom(brevoEmail);
            mimeMessageHelper.setTo(event.email());
            mimeMessageHelper.setSubject("Welcome to Amazio! Your Account Is Ready \uD83C\uDF89");
            mimeMessageHelper.setText(htmlContent, true);
            log.info("signup email triggered successfully \uD83D\uDCE8\uD83D\uDCE4✅");
        } catch (Exception e) {
            log.error("failed to send signup email \uD83D\uDCE8⛔❌",e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
        javaMailSender.send(mimeMessage);
    }

    public void handleUserUpdate(UserEvent event) throws MessagingException {
        log.trace("inside handle user update ");
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true,"UTF-8");
        Context context = new Context();

        context.setVariable("fName", event.fName());
        context.setVariable("lName",event.lName());
        context.setVariable("email",event.email());
        context.setVariable("userId",event.userId());
        context.setVariable("changePasswordUrl",changePasswordUrl);


        String htmlContent = templateEngine.process("user-update", context);

        try {
            mimeMessageHelper.setFrom(brevoEmail);
            mimeMessageHelper.setTo(event.email());
            mimeMessageHelper.setSubject("Your Amazio Account Details Have Been Updated");
            mimeMessageHelper.setText(htmlContent, true);
            log.info("user update email triggered successfully \uD83D\uDCE8\uD83D\uDCE4✅");

        } catch (Exception e) {
            log.error("failed to send user update email \uD83D\uDCE8⛔❌",e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
        javaMailSender.send(mimeMessage);
    }

    public void handleUserLoginFailed(UserEvent event) throws MessagingException {
        log.trace("inside handle user login failed");
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true,"UTF-8");
        Context context = new Context();

        context.setVariable("fName", event.fName());
        context.setVariable("lName",event.lName());
        context.setVariable("email",event.email());
        context.setVariable("userId",event.userId());
        context.setVariable("changePasswordUrl",changePasswordUrl);


        String htmlContent = templateEngine.process("login-failed", context);

        try {
            mimeMessageHelper.setFrom(brevoEmail);
            mimeMessageHelper.setTo(event.email());
            mimeMessageHelper.setSubject("Security Alert: Failed Login Attempt on Your Amazio Account");
            mimeMessageHelper.setText(htmlContent, true);
            log.info("login failed email triggered successfully \uD83D\uDCE8\uD83D\uDCE4✅");

        } catch (Exception e) {
            log.error("failed to send login-failed email \uD83D\uDCE8⛔❌",e);
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
        javaMailSender.send(mimeMessage);
    }
}
