package com.amazio.user_service.service;

import com.amazio.user_service.entity.User;
import com.amazio.user_service.enums.UserRole;
import com.amazio.user_service.exception.AccessDeniedException;
import com.amazio.user_service.exception.UserNotFoundException;
import com.amazio.user_service.kafka.UserEventProducer;
import com.amazio.user_service.repository.UserRepo;
import com.amazio.user_service.security.jwt.JwtService;
import com.amazio.user_service.dto.LoginRequestDto;
import com.amazio.user_service.dto.LoginResponseDto;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserEventProducer userEventProducer;

    public LoginResponseDto authenticateCustomer(
            LoginRequestDto request
    ) {
        return authenticate(request, UserRole.CUSTOMER);
    }

    public LoginResponseDto authenticateAdmin(
            LoginRequestDto request
    ) {
        return authenticate(request, UserRole.ADMIN);
    }

    private LoginResponseDto authenticate(
            LoginRequestDto request,
            UserRole expectedRole
    ) {

        User user = userRepo.findByEmail(request.email())
                .orElse(null);

        // Return a generic failure for an unknown email.
        if (user == null) {
            publishFailedLogin(request.email());

            throw new UserNotFoundException(
                    "User doesn't exist!"
            );
        }

        // Check the password before allowing login.
        if (!passwordEncoder.matches(
                request.password(),
                user.getPassword()
        )) {
            publishFailedLogin(user.getEmail());

            throw new BadCredentialsException(
                    "Invalid password!"
            );
        }

        // Enforce the requested login role.
        if (user.getUserRole() != expectedRole) {
            publishFailedLogin(user.getEmail());

            throw new AccessDeniedException(
                    "This login is only for customers!"
            );
        }

        String token = jwtService.generateJwtToken(user);

        userEventProducer.publishUserEvent(
                user.getUserId(),
                user.getEmail(),
                "USER_LOGIN",
                "User logged in successfully"
        );

        return new LoginResponseDto(
                token,
                "Bearer",
                jwtService.getExpirationTime(),
                user.getUserId(),
                user.getEmail(),
                user.getUserRole().name()
        );
    }

    private void publishFailedLogin(String email) {
        userEventProducer.publishUserEvent(
                null,
                email,
                "USER_LOGIN_FAILED",
                "Login attempt failed"
        );
    }
}