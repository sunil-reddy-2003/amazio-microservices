package com.amazio.user_service.controller;

import com.amazio.user_service.dto.*;
import com.amazio.user_service.exception.AccessDeniedException;
import com.amazio.user_service.exception.UserAlreadyExistsException;
import com.amazio.user_service.exception.UserNotFoundException;
import com.amazio.user_service.service.AuthenticationService;
import com.amazio.user_service.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "http://localhost:5173")
@Slf4j
@RequiredArgsConstructor
public class AuthApi {


    private final UserService userService;
    private final AuthenticationService authenticationService;



    @PostMapping("/signup")
    public ResponseEntity<Map<String,Object>> registerUser(@RequestBody RegisterUserDto registerUser) {
        try{
            userService.registerUser(registerUser);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            Map.of(
                                    "success",true,
                                    "message","Signed up successfully!!"));
        } catch (UserAlreadyExistsException e) {
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            Map.of(
                                    "success",false,
                                    "message",e.getMessage()));
        }
    }


    @PostMapping("/login")
    public ResponseEntity<Map<String,Object>> login(@RequestBody LoginRequestDto request) {
        try {
            LoginResponseDto response=authenticationService.authenticateCustomer(request);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(
                            Map.of(
                                    "success", true,
                                    "message", "Customer logged in successfully",
                                    "data", response));
        }
        catch (UserNotFoundException | BadCredentialsException e){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of(
                                    "success", false,
                                    "message", e.getMessage())
                    );
        }
        catch (AccessDeniedException e){
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(
                            Map.of(
                                    "success", false,
                                    "message", e.getMessage())
                    );
        }
    }


    @PostMapping("/adminlogin")
    public ResponseEntity<Map<String,Object>> adminLogin(@RequestBody LoginRequestDto request) {

        try {
            LoginResponseDto response=authenticationService.authenticateAdmin(request);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(
                            Map.of(
                                    "success", true,
                                    "message", "Customer logged in successfully",
                                    "data", response));
        }
        catch (UserNotFoundException | BadCredentialsException e){
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(
                            Map.of(
                                    "success", false,
                                    "message", e.getMessage())
                    );
        }
        catch (AccessDeniedException e){
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(
                            Map.of(
                                    "success", false,
                                    "message", e.getMessage())
                    );
        }
    }
}