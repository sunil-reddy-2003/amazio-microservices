package com.amazio.order_service.service;

import com.amazio.order_service.dto.*;
import com.amazio.order_service.entity.Order;
import com.amazio.order_service.entity.OrderItem;
import com.amazio.order_service.entity.Payment;
import com.amazio.order_service.enums.OrderStatus;
import com.amazio.order_service.enums.PaymentStatus;
import com.amazio.order_service.exception.OrderNotFoundException;
import com.amazio.order_service.exception.ProductNotFoundException;
import com.amazio.order_service.exception.UnIntendedUserException;
import com.amazio.order_service.kafka.OrderEventProducer;
import com.amazio.order_service.kafka.OrderNotificationEventProducer;
import com.amazio.order_service.repository.OrderRepo;

import com.amazio.order_service.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.web.client.RestClient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepo orderRepo;
    private final OrderEventProducer orderEventProducer;
    private final RestClient productRestClient;
    private final JwtService jwtService;
    private final RestClient userRestClient;
    private final OrderNotificationEventProducer orderNotificationEventProducer;

    @Transactional
    public Order createOrder(
            List<OrderItemsDto> orderItems,
            OrderAddressDto address,
            String paymentMethod,
            UUID userId,
            String authHeader) {

        if (userId == null) {
            throw new IllegalArgumentException("User ID is required");
        }

        if (orderItems == null || orderItems.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item");
        }

        if (address == null) {
            throw new IllegalArgumentException("Shipping address is required");
        }

        if (paymentMethod == null || paymentMethod.isBlank()) {
            throw new IllegalArgumentException("Payment method is required");
        }

        Order order = new Order();

        double totalPrice = 0;
        int totalQuantity = 0;

        List<OrderItem> items = new ArrayList<>();

        for (OrderItemsDto itemDto : orderItems) {

            if (itemDto.productId() == null || itemDto.productId() <= 0) {
                throw new IllegalArgumentException("Valid product ID is required");
            }

            if (itemDto.quantity() == null || itemDto.quantity() <= 0) {
                throw new IllegalArgumentException(
                        "Quantity must be greater than zero"
                );
            }
            ApiResponse response = productRestClient.get()
                    .uri("/api/v1/products/{productId}", itemDto.productId())
                    .header("Authorization",authHeader)
                    .retrieve()
                    .body(ApiResponse.class);
            ProductResponseDto product=response.data();
            if (product == null) {
                throw new ProductNotFoundException(
                        "Product not found: " + itemDto.productId()
                );
            }

            OrderItem orderItem = new OrderItem();

            orderItem.setProductId(itemDto.productId());
            orderItem.setProductName(product.name());
            orderItem.setPrice(product.price());
            orderItem.setImageUrl(product.imageUrl());
            orderItem.setQuantity(itemDto.quantity());
            orderItem.setOrder(order);

            totalPrice += product.price() * itemDto.quantity();
            totalQuantity += itemDto.quantity();

            items.add(orderItem);
        }
        LocalDateTime now = LocalDateTime.now();

        Payment payment = new Payment();
        payment.setPaymentStatus(PaymentStatus.PENDING);
        payment.setPaymentMethod(paymentMethod);
        payment.setAmountPaid(
                "Cash on Delivery".equalsIgnoreCase(paymentMethod) ? 0 : totalPrice
        );
        payment.setTransactionId(null);
        payment.setCreatedAt(now);
        payment.setUpdatedAt(now);
        payment.setOrder(order);

        order.setUserId(userId);
        order.setOrderStatus(OrderStatus.PLACED);
        order.setDate(LocalDate.now().toString());
        order.setTotalPrice(totalPrice);
        order.setTotalQuantity(totalQuantity);
        order.setOrderItems(items);
        order.setPayment(payment);
        order.setCreatedAt(now);
        order.setUpdatedAt(now);

        order.setPincode(address.pincode());
        order.setArea(address.area());
        order.setFlat(address.flat());
        order.setLandmark(address.landmark());
        order.setCity(address.city());
        order.setState(address.state());

        Order savedOrder = orderRepo.save(order);

        OrderCreatedEvent orderCreatedEvent = new OrderCreatedEvent(
                savedOrder.getOrderId(),
                savedOrder.getOrderItems()
                        .stream()
                        .map(item ->
                                new OrderCreatedEvent.OrderItemEvent(
                                        item.getProductId(),
                                        item.getQuantity()
                                )
                        )
                        .toList()
        );

//        orderEventProducer.publishOrderCreated(orderCreatedEvent);
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        orderEventProducer.publishOrderCreated(orderCreatedEvent);
                    }
                }
        );

        UserResponseDto user = userRestClient.get()
                .uri("/api/v1/users/userid/{userId}", userId)
                .header("Authorization",authHeader)
                .retrieve()
                .body(UserResponseDto.class);

        OrderNotificationEvent orderNotificationEvent=new OrderNotificationEvent(
                savedOrder.getOrderId(),
                userId,
                user.email(),
                user.fName()+" "+user.lName(),
                savedOrder.getOrderItems()
                        .stream()
                        .map(item ->
                                new OrderNotificationEvent.OrderItemDetails(
                                        item.getProductId(),
                                        item.getProductName(),
                                        item.getImageUrl(),
                                        item.getQuantity(),
                                        item.getPrice()
                                )
                        )
                        .toList(),
                savedOrder.getTotalPrice(),
                "ORDER_NOTIFICATION",
                LocalDateTime.now()
        );

//        orderNotificationEventProducer.publishOrderCreated(orderNotificationEvent);
        TransactionSynchronizationManager.registerSynchronization(
                new TransactionSynchronization() {
                    @Override
                    public void afterCommit() {
                        orderNotificationEventProducer
                                .publishNotification(orderNotificationEvent);
                    }
                }
        );
        return savedOrder;
    }

    @Transactional
    public void confirmOrder(UUID orderId) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found: " + orderId));

        order.setOrderStatus(OrderStatus.CONFIRMED);

        orderRepo.save(order);

        log.info("Order {} confirmed", orderId);
    }

    @Transactional
    public void rejectOrder(UUID orderId, String reason) {

        Order order = orderRepo.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException(
                                "Order not found: " + orderId));

        order.setOrderStatus(OrderStatus.REJECTED);

        orderRepo.save(order);

        log.warn("Order {} rejected: {}", orderId, reason);
    }

    public List<OrdersResponseDto> getOrdersWithUserId(UUID userId, String authHeader) {

        String token=authHeader.substring(7);
        UUID authorizedUserId= jwtService.extractUserId(token);

        if(!authorizedUserId.equals(userId)) throw new UnIntendedUserException("You are not authorized to access this order");

        List<Order> orders = orderRepo.findByUserId(userId);


        return orders.stream()
                .map(order -> new OrdersResponseDto(
                        order.getOrderId(),
                        order.getOrderStatus(),
                        order.getDate(),
                        order.getTotalPrice(),
                        order.getTotalQuantity()
                ))
                .toList();
    }

    @Transactional(readOnly = true)
    public OrderInfoResponseDto getOrderByOrderId(UUID orderId, String authHeader) {
        String token=authHeader.substring(7);
        UUID userId= jwtService.extractUserId(token);
        Order order = orderRepo.findByOrderIdAndUserId(orderId,userId)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found: " + orderId));

        List<OrderItemResponseDto> orderItemResponseDtos = new ArrayList<>();

        for (OrderItem item : order.getOrderItems()) {

            ApiResponse response = productRestClient.get()
                    .uri("/api/v1/products/{productId}", item.getProductId())
                    .header("Authorization",authHeader)
                    .retrieve()
                    .body(ApiResponse.class);
            ProductResponseDto product=response.data();

            if (product == null) {
                throw new ProductNotFoundException(
                        "Product not found: " + item.getProductId());
            }

            OrderItemResponseDto responseDto = new OrderItemResponseDto(
                    item.getProductId(),
                    item.getQuantity(),
                    product.imageUrl(),
                    item.getPrice()
            );

            orderItemResponseDtos.add(responseDto);
        }

        AddressResponseDto address = new AddressResponseDto(
                order.getPincode(),
                order.getArea(),
                order.getFlat(),
                order.getLandmark(),
                order.getCity(),
                order.getState()
        );

        Payment payment = order.getPayment();

        PaymentResponseDto paymentResponseDto = new PaymentResponseDto(
                payment.getPaymentStatus(),
                payment.getPaymentMethod(),
                payment.getTransactionId(),
                payment.getAmountPaid()
        );

        return new OrderInfoResponseDto(
                order.getOrderStatus(),
                order.getDate(),
                order.getTotalPrice(),
                order.getTotalQuantity(),
                orderItemResponseDtos,
                address,
                paymentResponseDto
        );
    }


}