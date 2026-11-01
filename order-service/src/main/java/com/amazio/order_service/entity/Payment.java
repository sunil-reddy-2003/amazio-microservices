package com.amazio.order_service.entity;

import com.amazio.order_service.enums.PaymentStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Payment {

    @Id
    @Column(unique = true,nullable = false)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long paymentId;

    @Column(nullable = false)
    @Enumerated(value = EnumType.STRING)
    private PaymentStatus paymentStatus;

    @Column(nullable = false)
    private String paymentMethod;

    @Column(nullable = true)
    private String transactionId;

    @Column(nullable = false)
    private double amountPaid;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @JsonBackReference
    @OneToOne
    @JoinColumn(name = "orderId")
    private Order order;

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", paymentStatus=" + paymentStatus +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", transactionId='" + transactionId + '\'' +
                ", amountPaid=" + amountPaid +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                ", order=" + order +
                '}';
    }
}
