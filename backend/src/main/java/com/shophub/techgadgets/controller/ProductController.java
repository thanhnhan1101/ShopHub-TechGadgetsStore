package com.shophub.techgadgets.controller;

import com.shophub.techgadgets.dto.ProductRequest;
import com.shophub.techgadgets.entity.Product;
import com.shophub.techgadgets.entity.ProductImage;
import com.shophub.techgadgets.repository.CategoryRepository;
import com.shophub.techgadgets.repository.ProductRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = "http://localhost:5173")
public class ProductController {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductController(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @GetMapping
    public List<Product> getAllProducts(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false, defaultValue = "false") Boolean includeInactive) {
        
        if (categoryId != null) {
            // Nếu includeInactive = true (admin), lấy tất cả
            if (includeInactive) {
                return productRepository.findByCategoryId(categoryId);
            }
            // Nếu false (user), chỉ lấy active
            return productRepository.findByCategoryIdAndIsActiveTrue(categoryId);
        }
        
        // Lấy tất cả products (admin) hoặc chỉ active (user)
        if (includeInactive) {
            return productRepository.findAll();
        }
        return productRepository.findByIsActiveTrue();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Integer id) {
        return productRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody ProductRequest request) {
        // Validate category
        if (request.getCategoryId() == null) {
            return ResponseEntity.badRequest().build();
        }
        
        // Validate price and stock
        if (request.getPrice() == null || request.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseEntity.badRequest().build();
        }
        
        if (request.getStock() == null || request.getStock() < 0) {
            return ResponseEntity.badRequest().build();
        }
        
        return categoryRepository.findById(request.getCategoryId())
                .map(category -> {
                    Product product = new Product();
                    product.setCategory(category);
                    product.setName(request.getName());
                    product.setDescription(request.getDescription());
                    product.setPrice(request.getPrice());
                    product.setStock(request.getStock());
                    product.setIsActive(request.getIsActive());
                    
                    // Xử lý nhiều ảnh
                    List<String> imageUrls = request.getImageUrls();
                    if (imageUrls != null && !imageUrls.isEmpty()) {
                        for (int i = 0; i < imageUrls.size(); i++) {
                            ProductImage image = new ProductImage();
                            image.setImageUrl(imageUrls.get(i));
                            image.setDisplayOrder(i);
                            image.setIsPrimary(i == 0); // Ảnh đầu tiên là ảnh chính
                            product.addImage(image);
                        }
                    } else if (request.getImageUrl() != null && !request.getImageUrl().isEmpty()) {
                        // Backward compatibility: hỗ trợ imageUrl đơn
                        product.setImageUrl(request.getImageUrl());
                    }
                    
                    return ResponseEntity.ok(productRepository.save(product));
                })
                .orElse(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Product> updateProduct(@PathVariable Integer id, @RequestBody ProductRequest request) {
        return productRepository.findById(id)
                .map(product -> {
                    product.setName(request.getName());
                    product.setDescription(request.getDescription());
                    product.setPrice(request.getPrice());
                    product.setStock(request.getStock());
                    product.setIsActive(request.getIsActive());
                    
                    if (request.getCategoryId() != null) {
                        categoryRepository.findById(request.getCategoryId())
                                .ifPresent(product::setCategory);
                    }
                    
                    // Xử lý cập nhật ảnh
                    List<String> imageUrls = request.getImageUrls();
                    if (imageUrls != null && !imageUrls.isEmpty()) {
                        // Xóa tất cả ảnh cũ
                        product.clearImages();
                        
                        // Thêm ảnh mới
                        for (int i = 0; i < imageUrls.size(); i++) {
                            ProductImage image = new ProductImage();
                            image.setImageUrl(imageUrls.get(i));
                            image.setDisplayOrder(i);
                            image.setIsPrimary(i == 0);
                            product.addImage(image);
                        }
                    } else if (request.getImageUrl() != null) {
                        // Backward compatibility
                        product.setImageUrl(request.getImageUrl());
                    }
                    
                    return ResponseEntity.ok(productRepository.save(product));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        return productRepository.findById(id)
                .map(product -> {
                    productRepository.delete(product); // Hard delete - xóa hẳn khỏi database
                    return ResponseEntity.noContent().<Void>build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
