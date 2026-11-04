package com.amazio.user_service.controller;

import com.amazio.user_service.dto.*;
import com.amazio.user_service.entity.UserAddress;
import com.amazio.user_service.exception.AddressNotFoundException;
import com.amazio.user_service.exception.UserNotFoundException;
import com.amazio.user_service.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/v1/addresses")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
@Slf4j
public class AddressApi {


    private final UserService userService;

    @PostMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> saveAddress(
            @PathVariable UUID userId,
            @RequestBody UserAddressDto addressDto) {
        try {
            UserAddress savedAddress = userService.saveAddress(userId, addressDto);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            Map.of(
                                    "success",true,
                                    "message", "Address saved successfully",
                                    "data", savedAddress
                            )
                    );
        }
        catch (UserNotFoundException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of(
                                    "success",false,
                                    "message", e.getMessage()
                            ));
        }catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Map.of(
                                    "success",false,
                                    "message",e.getMessage()));
        }
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Map<String, Object>> getAddresses(@PathVariable UUID userId) {
        try {
            List<UserAddress> addresses = userService.getAddresses(userId);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(
                            Map.of(
                                    "success", true,
                                    "data", addresses,
                                    "message","Address retrieved successfully"
                            )
                    );
        }catch (UserNotFoundException | AddressNotFoundException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of(
                                    "success", false,
                                    "message", e.getMessage()
                            )
                    );
        }catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            Map.of(
                                    "success",false,
                                    "message",e.getMessage()));
        }
    }


    @PutMapping("/{userId}/{id}")
    public ResponseEntity<Map<String, Object>> updateAddress(
            @PathVariable UUID userId,
            @PathVariable int id,
            @RequestBody AddressRequestDto addressRequest
    ) {

        try {
            UserAddress updatedAddress = userService.updateAddress(userId, id, addressRequest);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(
                            Map.of(
                                    "success",true,
                                    "message", "Address updated successfully",
                                    "data", updatedAddress
                            )
                    );
        }
        catch (UserNotFoundException | AddressNotFoundException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of(
                                    "success",false,
                                    "message", e.getMessage()
                            )
                    );
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            Map.of(
                                    "success",false,
                                    "message", e.getMessage()
                            )
                    );
        }
    }

    @DeleteMapping("/{userId}/{id}")
    public ResponseEntity<Map<String, Object>> deleteAddress(
            @PathVariable UUID userId,
            @PathVariable int id
    ) {
        try {
            userService.deleteAddress(userId, id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(
                            Map.of(
                                    "success",true,
                                    "message", "Address deleted successfully")
                    );
        }catch (AddressNotFoundException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of(
                                    "success",false,
                                    "message", e.getMessage())
                    );
        }catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            Map.of(
                                    "success",false,
                                    "message",e.getMessage()));
        }

    }


}