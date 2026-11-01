package com.amazio.product_service.repository;


import com.amazio.product_service.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ProductRepo extends JpaRepository<Product,Integer> {


    Page<Product> findByNameContainingIgnoreCase(
            String name, Pageable pageable);

    Page<Product> findByNameContainingIgnoreCaseAndCategoryIn(
            String name, List<String> categories, Pageable pageable);

}
