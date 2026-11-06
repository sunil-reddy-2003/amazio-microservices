package com.amazio.product_service.service;



import com.amazio.product_service.dto.ProductRequestDto;
import com.amazio.product_service.entity.Product;
import com.amazio.product_service.exception.ProductNotFoundException;
import com.amazio.product_service.kafka.ProductEventProducer;
import com.amazio.product_service.repository.ProductRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Slf4j
public class ProductService {

    private final ProductRepo productRepo;
    private final ProductEventProducer productEventProducer;

    @Autowired
    public ProductService(
            ProductRepo productRepo,
            ProductEventProducer productEventProducer
    ) {
        this.productRepo = productRepo;
        this.productEventProducer = productEventProducer;
    }

    @Transactional
    public Product addProduct(ProductRequestDto product) {

        Product saveProduct = new Product();

        saveProduct.setName(product.getName());
        saveProduct.setImageUrl(product.getImageUrl());
        saveProduct.setPrice(product.getPrice());
        saveProduct.setDescription(product.getDescription());
        saveProduct.setCategory(product.getCategory());

        saveProduct.setCreatedAt(LocalDateTime.now());
        saveProduct.setUpdatedAt(LocalDateTime.now());

        Product savedProduct = productRepo.save(saveProduct);

        productEventProducer.publishProductCreated(savedProduct.getId());

        return savedProduct;
    }

    @Transactional(readOnly = true)
    public Product getProductById(int productId) {

        return productRepo.findById(productId)
                .orElseThrow(() ->
                        new ProductNotFoundException(
                                "No product found with id: " + productId
                        )
                );
    }

    @Transactional
    public Page<Product> getAllProducts(
            int page,
            int size,
            String search,
            List<String> category) {

        validatePagination(page, size);

        Pageable pageable = PageRequest.of(
                page,
                size,
                Sort.by("price").ascending());

        String searchTerm = search == null ? "" : search;

        if (category == null || category.isEmpty()) {
            return productRepo.findByNameContainingIgnoreCase(
                    searchTerm, pageable);
        }

        return productRepo.findByNameContainingIgnoreCaseAndCategoryIn(
                searchTerm, category, pageable);
    }

    @Transactional
    public Page<Product> viewAllProducts(
            int page,
            int size,
            String search,
            List<String> category) {

        validatePagination(page, size);

        Pageable pageable = PageRequest.of(page, size);
        String searchTerm = search == null ? "" : search;

        if (category == null || category.isEmpty()) {
            return productRepo.findByNameContainingIgnoreCase(
                    searchTerm, pageable);
        }

        return productRepo.findByNameContainingIgnoreCaseAndCategoryIn(
                searchTerm, category, pageable);
    }

    @Transactional
    public Product updateProduct(int id, ProductRequestDto request) {

        Product product = getProductById(id);

        product.setName(request.getName());
        product.setCategory(request.getCategory());
        product.setPrice(request.getPrice());
        product.setDescription(request.getDescription());
        product.setImageUrl(request.getImageUrl());
        product.setUpdatedAt(LocalDateTime.now());

        return productRepo.save(product);
    }

    @Transactional
    public void deleteProduct(int id) {

        Product product = getProductById(id);
        productRepo.delete(product);
    }

    private void validatePagination(int page, int size) {

        if (page < 0 || size <= 0) {
            throw new IllegalArgumentException(
                    "Page must be >= 0 and size must be > 0");
        }
    }
}
