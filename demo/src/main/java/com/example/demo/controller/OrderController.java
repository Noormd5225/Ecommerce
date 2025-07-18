package com.example.demo.controller;

import com.example.demo.entity.CartItem;
import com.example.demo.entity.Order;
import com.example.demo.entity.User;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.OrderRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user/order")
@CrossOrigin("*")
public class OrderController {
    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @PostMapping("/checkout")
    public ResponseEntity<String> checkout(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        List<CartItem> cartItems = cartRepository.findByUser(user);

        if (cartItems.isEmpty()) {
            return ResponseEntity.badRequest().body("Cart is empty");
        }

        double total = cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        Order order = new Order();
        order.setUser(user);
        order.setItems(cartItems);
        order.setTotalPrice(total);
        orderRepository.save(order);

        cartRepository.deleteByUser(user);

        return ResponseEntity.ok("Order placed successfully");
    }

    @GetMapping
    public List<Order> getOrders(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow();
        return orderRepository.findByUser(user);
    }
}
