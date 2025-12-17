package com.shophub.techgadgets.service;

import com.shophub.techgadgets.entity.Product;
import com.shophub.techgadgets.repository.CategoryRepository;
import com.shophub.techgadgets.repository.ProductRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<Product> getAllProducts(Integer categoryId) {
        if (categoryId != null) {
            return productRepository.findByCategoryIdAndIsActiveTrue(categoryId);
        }
        return productRepository.findByIsActiveTrue();
    }

    public Product getProductById(Integer id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + id));
    }

    public Product createProduct(Product product) {
        // Validate category exists
        if (product.getCategory() == null || product.getCategory().getId() == null) {
            throw new RuntimeException("Category is required");
        }

        return categoryRepository.findById(product.getCategory().getId())
                .map(category -> {
                    product.setCategory(category);
                    return productRepository.save(product);
                })
                .orElseThrow(() -> new RuntimeException("Category not found"));
    }

    public Product updateProduct(Integer id, Product productDetails) {
        Product product = getProductById(id);
        
        product.setName(productDetails.getName());
        product.setDescription(productDetails.getDescription());
        product.setPrice(productDetails.getPrice());
        product.setStock(productDetails.getStock());
        product.setImageUrl(productDetails.getImageUrl());
        product.setIsActive(productDetails.getIsActive());

        if (productDetails.getCategory() != null && productDetails.getCategory().getId() != null) {
            categoryRepository.findById(productDetails.getCategory().getId())
                    .ifPresent(product::setCategory);
        }

        return productRepository.save(product);
    }

    public void deleteProduct(Integer id) {
        Product product = getProductById(id);
        product.setIsActive(false); // Soft delete
        productRepository.save(product);
    }
}
