package com.example.demo.repository;

import com.example.demo.entity.Product;
import org.springframework.data.domain.Page; // ✅ Correct import
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByNameContainingIgnoreCase(String name, Pageable pageable);
    Page<Product> findByCategoryContainingIgnoreCase(String category, Pageable pageable);
    Page<Product> findAll(Pageable pageable);
}
