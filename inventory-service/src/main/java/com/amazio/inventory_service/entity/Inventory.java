package com.amazio.inventory_service.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(nullable = false, unique = true)
    private int productId;

    @Column(nullable = false)
    @Check(constraints = "available_quantity >= 0")
    private int availableQuantity;

    @Column(nullable = false)
    private int reservedQuantity;

}