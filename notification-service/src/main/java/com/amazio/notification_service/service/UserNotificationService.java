package com.amazio.notification_service.service;

import com.amazio.notification_service.dto.UserEvent;
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
public class UserNotificationService {

    private final JavaMailSender javaMailSender;
    private final TemplateEngine templateEngine;

    public void handleUserLogin(UserEvent event) throws MessagingException {

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true,"UTF-8");
        Context context = new Context();

        context.setVariable("fName", event.fName());
        context.setVariable("lName",event.lName());
        context.setVariable("email",event.email());
        context.setVariable("timestamp",event.timestamp());
        context.setVariable("changePasswordUrl","http://localhost:5173/forgot-password");

        String htmlContent = templateEngine.process("login-success", context);

        try {
            mimeMessageHelper.setTo(event.email());
            mimeMessageHelper.setSubject("New Login Detected - Amazio");
            mimeMessageHelper.setText(htmlContent, true);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
        javaMailSender.send(mimeMessage);

    }

    public void handlePasswordReset(UserEvent event) throws MessagingException {
        log.info("inside handle user :"+event.toString());

        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true,"UTF-8");
        Context context = new Context();

        context.setVariable("fName", event.fName());
        context.setVariable("lName",event.lName());
        context.setVariable("email",event.email());
        context.setVariable("resetPasswordUrl","http://localhost:5173/reset-password?token="+event.message());

        String htmlContent = templateEngine.process("password-reset", context);

        try {
            mimeMessageHelper.setTo(event.email());
            mimeMessageHelper.setSubject("Reset Your Password - Amazio");
            mimeMessageHelper.setText(htmlContent, true);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
        javaMailSender.send(mimeMessage);
    }

    public void handleUserRegistration(UserEvent event) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true,"UTF-8");
        Context context = new Context();

        context.setVariable("fName", event.fName());
        context.setVariable("lName",event.lName());
        context.setVariable("email",event.email());
        context.setVariable("userId",event.userId());

        String htmlContent = templateEngine.process("signup", context);

        try {
            mimeMessageHelper.setTo(event.email());
            mimeMessageHelper.setSubject("Welcome to Amazio! Your Account Is Ready \uD83C\uDF89");
            mimeMessageHelper.setText(htmlContent, true);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
        javaMailSender.send(mimeMessage);
    }

    public void handleUserUpdate(UserEvent event) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true,"UTF-8");
        Context context = new Context();

        context.setVariable("fName", event.fName());
        context.setVariable("lName",event.lName());
        context.setVariable("email",event.email());
        context.setVariable("userId",event.userId());

        String htmlContent = templateEngine.process("user-update", context);

        try {
            mimeMessageHelper.setTo(event.email());
            mimeMessageHelper.setSubject("Your Amazio Account Details Have Been Updated");
            mimeMessageHelper.setText(htmlContent, true);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
        javaMailSender.send(mimeMessage);
    }

    public void handleUserLoginFailed(UserEvent event) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();

        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true,"UTF-8");
        Context context = new Context();

        context.setVariable("fName", event.fName());
        context.setVariable("lName",event.lName());
        context.setVariable("email",event.email());
        context.setVariable("userId",event.userId());

        String htmlContent = templateEngine.process("login-failed", context);

        try {
            mimeMessageHelper.setTo(event.email());
            mimeMessageHelper.setSubject("Security Alert: Failed Login Attempt on Your Amazio Account");
            mimeMessageHelper.setText(htmlContent, true);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email: " + e.getMessage(), e);
        }
        javaMailSender.send(mimeMessage);
    }
}
