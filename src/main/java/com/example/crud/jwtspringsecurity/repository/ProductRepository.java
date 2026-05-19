package com.example.crud.jwtspringsecurity.repository;


import com.example.crud.jwtspringsecurity.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA Repository for Product entities.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /** Find all products in a specific category */
    List<Product> findByCategory(String category);

    /** Find all currently available products */
    List<Product> findByIsAvailableTrue();

    /** Search products by name (case-insensitive, partial match) */
    List<Product> findByNameContainingIgnoreCase(String name);
}
