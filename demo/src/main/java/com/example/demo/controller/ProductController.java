package com.example.demo.controller;

import com.example.demo.dto.ProductDTO;
import com.example.demo.entity.Product;
import com.example.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api")
@CrossOrigin("*")
public class ProductController {

    @Autowired
    private ProductRepository productRepository;

    @PostMapping("/admin/products")
    public ResponseEntity<String> addProduct(@RequestBody ProductDTO dto) {
        Product product = new Product(dto.getName(), dto.getCategory(), dto.getPrice(), dto.getDescription());
        productRepository.save(product);
        return ResponseEntity.ok("Product added successfully.");
    }

    @PutMapping("/admin/products/{id}")
    public ResponseEntity<String> updateProduct(@PathVariable Long id, @RequestBody ProductDTO dto) {
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isEmpty()) return ResponseEntity.badRequest().body("Product not found");

        Product product = productOpt.get();
        product.setName(dto.getName());
        product.setCategory(dto.getCategory());
        product.setPrice(dto.getPrice());
        product.setDescription(dto.getDescription());
        productRepository.save(product);
        return ResponseEntity.ok("Product updated successfully.");
    }

    @DeleteMapping("/admin/products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        if (!productRepository.existsById(id)) return ResponseEntity.badRequest().body("Product not found");
        productRepository.deleteById(id);
        return ResponseEntity.ok("Product deleted.");
    }

    @GetMapping("/user/products")
    public Page<ProductDTO> getProducts(@RequestParam(defaultValue = "0") int page,
                                        @RequestParam(defaultValue = "5") int size,
                                        @RequestParam(required = false) String name,
                                        @RequestParam(required = false) String category) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> products;

        if (name != null) {
            products = productRepository.findByNameContainingIgnoreCase(name, pageable);
        } else if (category != null) {
            products = productRepository.findByCategoryContainingIgnoreCase(category, pageable);
        } else {
            products = productRepository.findAll(pageable);
        }

        return products.map(this::convertToDTO);
    }

    private ProductDTO convertToDTO(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setId(product.getId());
        dto.setName(product.getName());
        dto.setDescription(product.getDescription());
        dto.setCategory(product.getCategory());
        dto.setPrice(product.getPrice());
        return dto;
    }
}
