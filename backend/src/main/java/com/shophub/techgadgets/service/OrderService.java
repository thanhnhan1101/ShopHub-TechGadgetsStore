package com.shophub.techgadgets.service;

import com.shophub.techgadgets.entity.Order;
import com.shophub.techgadgets.entity.User;
import com.shophub.techgadgets.repository.OrderRepository;
import com.shophub.techgadgets.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderService(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    public List<Order> getAllOrders(String status) {
        if (status != null) {
            return orderRepository.findByStatusOrderByOrderDateDesc(status);
        }
        return orderRepository.findAll();
    }

    public Order getOrderById(Integer id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found with id: " + id));
    }

    public List<Order> getOrdersByUserId(Integer userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
    }

    public Order createOrder(Order order) {
        if (order.getUser() == null || order.getUser().getId() == null) {
            throw new RuntimeException("User is required");
        }

        User user = userRepository.findById(order.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        order.setUser(user);
        return orderRepository.save(order);
    }

    public Order updateOrderStatus(Integer id, String status) {
        Order order = getOrderById(id);
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public Order updateOrder(Integer id, Order orderDetails) {
        Order order = getOrderById(id);

        if (orderDetails.getStatus() != null) {
            order.setStatus(orderDetails.getStatus());
        }
        if (orderDetails.getTotalAmount() != null) {
            order.setTotalAmount(orderDetails.getTotalAmount());
        }
        if (orderDetails.getAddress() != null) {
            order.setAddress(orderDetails.getAddress());
        }

        return orderRepository.save(order);
    }
}
