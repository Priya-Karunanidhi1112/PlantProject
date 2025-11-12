package com.example.planting.repository;

import com.example.planting.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserIdAndOrderStatus(Long userId, String orderStatus);// For 'In Cart'
}

