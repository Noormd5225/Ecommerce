package com.example.demo.controller;

import com.example.demo.dto.CartItemDTO;
import com.example.demo.entity.CartItem;
import com.example.demo.entity.Product;
import com.example.demo.entity.User;
import com.example.demo.repository.CartRepository;
import com.example.demo.repository.ProductRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/user/cart")
@CrossOrigin("*")
public class CartController {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;


    @PostMapping("/add")
    public ResponseEntity<String> addToCart(@RequestBody CartItemDTO dto, Authentication auth) {

        User user = userRepository.findByUsername(auth.getName()).orElseThrow(() ->
                new RuntimeException("User not found"));


        Product product = productRepository.findById(dto.getProductId()).orElseThrow(() ->
                new RuntimeException("Product not found"));


        CartItem item = new CartItem();
        item.setProduct(product);
        item.setQuantity(dto.getQuantity());
        item.setUser(user);
        cartRepository.save(item);

        return ResponseEntity.ok("Item added to cart");
    }


    @PutMapping("/update/{id}")
    public ResponseEntity<String> updateCart(@PathVariable Long id, @RequestBody CartItemDTO dto) {
        Optional<CartItem> itemOpt = cartRepository.findById(id);
        if (itemOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Item not found");
        }

        CartItem item = itemOpt.get();
        item.setQuantity(dto.getQuantity());
        cartRepository.save(item);

        return ResponseEntity.ok("Cart updated");
    }

    @DeleteMapping("/remove/{id}")
    public ResponseEntity<String> removeItem(@PathVariable Long id) {
        if (!cartRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("Item not found");
        }
        cartRepository.deleteById(id);
        return ResponseEntity.ok("Item removed");
    }


    @GetMapping
    public List<CartItem> getCart(Authentication auth) {
        User user = userRepository.findByUsername(auth.getName()).orElseThrow(() ->
                new RuntimeException("User not found"));
        return cartRepository.findByUser(user);
    }
}
