package com.example.planting.repository;

import com.example.planting.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByLabelIgnoreCase(String label);
    List<Product> findByCategoryIgnoreCase(String category);
    List<Product> findByCategory(String category);


}
