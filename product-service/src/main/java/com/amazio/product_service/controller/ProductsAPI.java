package com.amazio.product_service.controller;


import com.amazio.product_service.dto.ProductResponseDto;
import com.amazio.product_service.entity.Product;
import com.amazio.product_service.exception.ProductNotFoundException;
import com.amazio.product_service.service.ProductService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


@Slf4j
@RestController
@RequestMapping("/api/v1/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductsAPI {

    ProductService productService;
    @Autowired
    public ProductsAPI(ProductService productService) {
        this.productService=productService;
    }


    @GetMapping("/{productId}")
    public ResponseEntity<Map<String ,Object>> getProductById(@PathVariable int productId){
        try {
            Product dbProduct=productService.getProductById(productId);
            ProductResponseDto productResponse=new ProductResponseDto(
                    dbProduct.getId(),
                    dbProduct.getName(),
                    dbProduct.getPrice(),
                    dbProduct.getImageUrl()
            );
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(
                            Map.of(
                                    "success",true,
                                    "message","Product found successfully",
                                    "data",productResponse));
        }
        catch (ProductNotFoundException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(
                            Map.of("success",false,
                                    "message",e.getMessage()
                            ));
        }
    }

    @GetMapping("/")
    public ResponseEntity<Page<Product>> getAllProducts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) List<String> category) {

        return ResponseEntity.ok(
                productService.getAllProducts(page, size, search, category));
    }
}
