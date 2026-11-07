package com.amazio.order_service.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Value("${user-service-url}")
    private String userServiceUrl;
    @Value("${product-service-url}")
    private String productServiceUrl;

    @Bean
    public RestClient productRestClient() {
        return RestClient.builder()
                .baseUrl(productServiceUrl)
                .build();
    }

    @Bean
    public RestClient userRestClient() {
        return RestClient.builder()
                .baseUrl(userServiceUrl)
                .build();
    }
}