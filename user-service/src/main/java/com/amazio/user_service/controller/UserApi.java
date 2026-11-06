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
@Slf4j
@RequiredArgsConstructor
public class UserApi {


    private final UserService userService;


    @GetMapping("userid/{userId}")
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

    @PutMapping("/{userId}")
    public ResponseEntity<Map<String,Object>> updateUser(@PathVariable UUID userId,@RequestBody UserRequestDto userDetails) {
        try{
            User updatedUser = userService.updateUser(userId, userDetails);
            UserResponseDto user=new UserResponseDto(
                    updatedUser.getUserId(),
                    updatedUser.getEmail(),
                    updatedUser.getFName(),
                    updatedUser.getLName(),
                    updatedUser.getMobile()
            );

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(
                            Map.of(
                                    "success",true,
                                    "message", "User updated successfully",
                                    "data",user));
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
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            Map.of(
                                    "success",false,
                                    "message",e.getMessage())
                    );
        }
    }
}