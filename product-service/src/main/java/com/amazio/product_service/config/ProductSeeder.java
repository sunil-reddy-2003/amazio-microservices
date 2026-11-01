package com.amazio.product_service.config;


import com.amazio.product_service.entity.Product;
import com.amazio.product_service.kafka.ProductEventProducer;
import com.amazio.product_service.repository.ProductRepo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.List;

@Configuration
@Slf4j
public class ProductSeeder {

    private final ProductEventProducer productEventProducer;

    @Autowired
    public ProductSeeder(ProductEventProducer productEventProducer) {
        this.productEventProducer = productEventProducer;
    }

    @Bean
    CommandLineRunner seedProducts(ProductRepo productRepository) {
        return args -> {

            log.info("Product count = " + productRepository.count());

            if (productRepository.count() == 0) {

                ObjectMapper mapper = new ObjectMapper();
                InputStream inputStream =
                        new ClassPathResource("products.json").getInputStream();

                List<Product> products =
                        mapper.readValue(inputStream, new TypeReference<List<Product>>() {});

                for (Product product : products) {
                    product.setCreatedAt(LocalDateTime.now());
                    product.setUpdatedAt(LocalDateTime.now());
                }

                List<Product> savedProducts=productRepository.saveAll(products);
                for(Product savedProduct: savedProducts){
                    productEventProducer.publishProductCreated(savedProduct.getId());
                }

                log.info("🔥 All products seeded successfully with zero quantity.");
            }
        };
    }
}