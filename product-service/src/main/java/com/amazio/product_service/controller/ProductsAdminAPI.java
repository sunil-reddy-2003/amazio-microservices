package com.amazio.product_service.controller;



import com.amazio.product_service.dto.ProductRequestDto;
import com.amazio.product_service.entity.Product;
import com.amazio.product_service.exception.ProductNotFoundException;
import com.amazio.product_service.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api/v1/admin/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductsAdminAPI {

    ProductService productService;

    @Autowired
    public ProductsAdminAPI(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/")
    public ResponseEntity<Map<String, Object>> addProduct(@RequestBody ProductRequestDto product) {
        try {
            Product dbProduct = productService.addProduct(product);
            return ResponseEntity
                    .status(HttpStatus.CREATED)
                    .body(
                            Map.of(
                                    "success", true,
                                    "message", "Product saved successfully",
                                    "data", dbProduct));
        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(
                            Map.of(
                                    "success", false,
                                    "message", "Failed to save product"));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> updateProduct(
            @PathVariable int id,
            @RequestBody ProductRequestDto product) {

        try {
            Product updatedProduct = productService.updateProduct(id, product);
            return ResponseEntity.status(HttpStatus.OK).body(
                    Map.of("success", true,
                            "message", "product updated successfully",
                            "data", updatedProduct)
            );
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of("success", false,
                            "message", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of("success", false,
                            "message", "Failed to update product")
            );
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteProduct(@PathVariable int id) {
        try {
            productService.deleteProduct(id);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(
                            Map.of(
                                    "success", true,
                                    "message", "Product deleted successfully"));
        } catch (ProductNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    Map.of(
                            "success", false,
                            "message", e.getMessage())
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of(
                            "success", false,
                            "message", "Failed to delete product")
            );

        }
    }
}