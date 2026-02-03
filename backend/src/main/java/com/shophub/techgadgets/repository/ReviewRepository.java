package com.shophub.techgadgets.repository;

import com.shophub.techgadgets.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Integer> {
    
    /**
     * Lấy tất cả đánh giá của 1 sản phẩm, sắp xếp theo ngày mới nhất
     */
    List<Review> findByProductIdOrderByCreatedAtDesc(Integer productId);
    
    /**
     * Lấy đánh giá của user cho 1 sản phẩm trong 1 đơn hàng
     */
    Optional<Review> findByUserIdAndProductIdAndOrderId(
        Integer userId, 
        Integer productId, 
        Integer orderId
    );
    
    /**
     * Kiểm tra user đã đánh giá sản phẩm trong đơn hàng chưa
     */
    boolean existsByUserIdAndProductIdAndOrderId(
        Integer userId, 
        Integer productId, 
        Integer orderId
    );
    
    /**
     * Lấy tất cả đánh giá của user
     */
    List<Review> findByUserIdOrderByCreatedAtDesc(Integer userId);
    
    /**
     * Tính trung bình rating của sản phẩm
     */
    @Query("SELECT AVG(r.rating) FROM Review r WHERE r.product.id = :productId")
    Double getAverageRatingByProductId(@Param("productId") Integer productId);
    
    /**
     * Đếm tổng số đánh giá của sản phẩm
     */
    Long countByProductId(Integer productId);
    
    /**
     * Đếm số lượng đánh giá theo từng rating (1-5 sao)
     */
    @Query("SELECT r.rating, COUNT(r) FROM Review r WHERE r.product.id = :productId GROUP BY r.rating ORDER BY r.rating DESC")
    List<Object[]> getRatingDistributionByProductId(@Param("productId") Integer productId);
}
