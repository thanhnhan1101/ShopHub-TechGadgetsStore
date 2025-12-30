package com.shophub.techgadgets.repository;

import com.shophub.techgadgets.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Integer> {
    List<Product> findByIsActiveTrue();
    List<Product> findByCategoryIdAndIsActiveTrue(Integer categoryId);
    List<Product> findByCategoryId(Integer categoryId); // Lấy tất cả products theo category (cho admin)
}
