package com.shophub.techgadgets.repository;

import com.shophub.techgadgets.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {
    
    /**
     * Tìm tất cả ảnh của sản phẩm, sắp xếp theo displayOrder
     */
    List<ProductImage> findByProductIdOrderByDisplayOrderAsc(Integer productId);
    
    /**
     * Xóa tất cả ảnh của sản phẩm
     */
    void deleteByProductId(Integer productId);
    
    /**
     * Tìm ảnh chính của sản phẩm
     */
    ProductImage findByProductIdAndIsPrimaryTrue(Integer productId);
}
