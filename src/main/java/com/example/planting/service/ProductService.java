package com.example.planting.service;

import com.example.planting.model.Product;
import com.example.planting.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepo;

    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public List<Product> getByLabel(String label) {
        return productRepo.findByLabelIgnoreCase(label);
    }

    public List<Product> getByCategoryOnly(String category) {
        return productRepo.findByCategoryIgnoreCase(category);
    }

    public List<Product> getByCategory(String category) {
        return productRepo.findByCategoryIgnoreCase(category);
    }

    public void save(Product product) {
        productRepo.save(product);
    }

    public Product getById(Long id) {
        return productRepo.findById(id).orElse(null);
    }

    public List<Product> getAll() {
        return productRepo.findAll();
    }

    public void deleteById(Long id) {
        productRepo.deleteById(id);
    }
}
