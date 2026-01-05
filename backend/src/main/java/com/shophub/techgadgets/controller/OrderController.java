package com.shophub.techgadgets.controller;

import com.shophub.techgadgets.entity.Order;
import com.shophub.techgadgets.repository.OrderRepository;
import com.shophub.techgadgets.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
@CrossOrigin(origins = "http://localhost:5173")
public class OrderController {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;

    public OrderController(OrderRepository orderRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Order> getAllOrders(@RequestParam(required = false) String status) {
        if (status != null) {
            return orderRepository.findByStatusOrderByOrderDateDesc(status);
        }
        return orderRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Integer id) {
        return orderRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public List<Order> getOrdersByUserId(@PathVariable Integer userId) {
        return orderRepository.findByUserIdOrderByOrderDateDesc(userId);
    }

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        if (order.getUser() == null || order.getUser().getId() == null) {
            return ResponseEntity.badRequest().build();
        }

        return userRepository.findById(order.getUser().getId())
                .map(user -> {
                    order.setUser(user);
                    
                    // Set bidirectional relationship for OrderItems
                    if (order.getOrderItems() != null) {
                        order.getOrderItems().forEach(item -> item.setOrder(order));
                    }
                    
                    return ResponseEntity.ok(orderRepository.save(order));
                })
                .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Integer id, @RequestParam String status) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.setStatus(status);
                    return ResponseEntity.ok(orderRepository.save(order));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> cancelOrder(@PathVariable Integer id) {
        return orderRepository.findById(id)
                .map(order -> {
                    order.setStatus("CANCELLED");
                    orderRepository.save(order);
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
