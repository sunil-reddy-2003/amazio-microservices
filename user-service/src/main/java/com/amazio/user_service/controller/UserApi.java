package com.amazio.user_service.controller;

import com.amazio.user_service.dto.*;
import com.amazio.user_service.entity.User;
import com.amazio.user_service.exception.UserAlreadyExistsException;
import com.amazio.user_service.exception.UserNotFoundException;
import com.amazio.user_service.mapper.UserResponseMapper;
import com.amazio.user_service.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/users")
@CrossOrigin(origins = "http://localhost:5173")
@Slf4j
@RequiredArgsConstructor
public class UserApi {


    private final UserService userService;


    @GetMapping("/{userId}")
    public ResponseEntity<UserResponseDto> getUserById(@PathVariable UUID userId) {
        return ResponseEntity.ok(
                UserResponseMapper.toDto(userService.getUserById(userId))
        );
    }


    @GetMapping("/{email}")
    public ResponseEntity<UserResponseDto> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(
                UserResponseMapper.toDto(userService.getUserByEmail(email))
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String,Object>> updateUser(@PathVariable UUID id,@RequestBody UserRequestDto userDetails) {
        try{
            User updatedUser = userService.updateUser(id, userDetails);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(
                            Map.of(
                                    "success",true,
                                    "message", "User updated successfully",
                                    "data",UserResponseMapper.toDto(updatedUser)));
        }
        catch (UserAlreadyExistsException e){
            return ResponseEntity
                    .status(HttpStatus.CONFLICT)
                    .body(
                            Map.of(
                                    "success",false,
                                    "message",e.getMessage())
                    );
        }
        catch (UserNotFoundException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of(
                                    "success",false,
                                    "message",e.getMessage())
                    );
        }
    }
}