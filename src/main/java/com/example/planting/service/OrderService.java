package com.example.planting.service;

import com.example.planting.model.Order;
import com.example.planting.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    public Order getOrCreateCart(Long userId) {
        List<Order> orders = orderRepository.findByUserIdAndOrderStatus(userId, "In Cart");

        if (orders != null && !orders.isEmpty()) {
            return orders.get(0); // return the first cart order if exists
        }

        // Create new cart order
        Order newOrder = new Order();
        newOrder.setUserId(userId);
        newOrder.setOrderStatus("In Cart");
        newOrder.setPaymentStatus("Pending");
        return orderRepository.save(newOrder);
    }


    public Order save(Order order) {
        return orderRepository.save(order);
    }

    public Optional<Order> getById(Long id) {
        return orderRepository.findById(id);
    }
}