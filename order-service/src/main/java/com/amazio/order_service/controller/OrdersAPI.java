package com.amazio.order_service.controller;

import com.amazio.order_service.dto.OrderInfoResponseDto;
import com.amazio.order_service.dto.OrderRequestDto;
import com.amazio.order_service.dto.OrdersResponseDto;
import com.amazio.order_service.entity.Order;
import com.amazio.order_service.exception.OrderNotFoundException;
import com.amazio.order_service.exception.ProductNotFoundException;
import com.amazio.order_service.exception.UnIntendedUserException;
import com.amazio.order_service.service.OrderService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/v1/orders")
@CrossOrigin(origins = "http://localhost:5173")
@RequiredArgsConstructor
public class OrdersAPI {

    private final OrderService orderService;

    @PostMapping("/")
    public ResponseEntity<Map<String,Object>> createOrder(
            @RequestBody OrderRequestDto orderRequestDto,
            @RequestHeader("Authorization") String authHeader) {

        log.info("inside createOrder endpoint ");

        try {
            Order order = orderService.createOrder(
                    orderRequestDto.orderItems(),
                    orderRequestDto.address(),
                    orderRequestDto.paymentMethod(),
                    orderRequestDto.userId(),
                    authHeader
            );
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            Map.of(
                                    "success",true,
                                    "message","Order created successfully",
                                    "data",order));
        }catch (ProductNotFoundException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of(
                                    "success",false,
                                    "message",e.getMessage()));
        }catch (IllegalArgumentException e){
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(
                            Map.of(
                                    "success",false,
                                    "message",e.getMessage()));
        }
        catch (Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                        Map.of(
                            "success",false,
                            "message",e.getMessage()));
        }

    }

    @GetMapping("userid/{userId}")
    public ResponseEntity<Map<String,Object>> getOrdersWithUserId(
            @PathVariable UUID userId,@RequestHeader("Authorization") String authHeader) {


        try{
            List<OrdersResponseDto> orders =
                    orderService.getOrdersWithUserId(userId, authHeader);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(
                            Map.of(
                                    "success", true,
                                    "message", "Orders retrieved successfully",
                                    "data", orders
                            ));
        }
        catch (UnIntendedUserException e){
            return ResponseEntity
                    .status(HttpStatus.FORBIDDEN)
                    .body(
                            Map.of(
                                    "success",false,
                                    "message",e.getMessage()
                            )
                    );
        }
    }

    @GetMapping("orderid/{orderId}")
    public ResponseEntity<Map<String ,Object>> getOrderByOrderId(
            @PathVariable UUID orderId,
            @RequestHeader("Authorization") String authHeader){
        try{
            OrderInfoResponseDto order = orderService.getOrderByOrderId(orderId, authHeader);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(
                            Map.of(
                                    "success",true,
                                    "message","Order retrieved successfully",
                                    "data",order
                            ));
        } catch (OrderNotFoundException | ProductNotFoundException e) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of(
                                    "success",false,
                                    "message",e.getMessage()
                            ));
        }
    }
}