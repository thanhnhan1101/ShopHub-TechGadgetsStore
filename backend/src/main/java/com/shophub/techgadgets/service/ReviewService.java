package com.shophub.techgadgets.service;

import com.shophub.techgadgets.dto.ReviewRequest;
import com.shophub.techgadgets.entity.*;
import com.shophub.techgadgets.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
public class ReviewService {
    
    // Các trạng thái cho phép đánh giá
    private static final Set<String> REVIEWABLE_STATUSES = Set.of(
        "PAID",           // Đã thanh toán
        "SHIPPING",       // Đang giao hàng  
        "DELIVERED"       // Đã giao hàng
    );
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private ProductRepository productRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Tạo đánh giá mới
     */
    @Transactional
    public Review createReview(Integer userId, ReviewRequest request) {
        // 1. Validate: Order phải tồn tại
        Order order = orderRepository.findById(request.getOrderId())
            .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));
        
        // 2. Validate: Order phải thuộc về user
        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Đơn hàng không thuộc về bạn");
        }
        
        // 3. Validate: Order phải ĐÃ THANH TOÁN trở lên
        if (!REVIEWABLE_STATUSES.contains(order.getStatus())) {
            throw new RuntimeException("Chỉ có thể đánh giá sau khi đơn hàng đã thanh toán");
        }
        
        // 4. Validate: Sản phẩm phải có trong đơn hàng
        boolean productInOrder = order.getOrderItems().stream()
            .anyMatch(item -> item.getProduct().getId().equals(request.getProductId()));
        
        if (!productInOrder) {
            throw new RuntimeException("Sản phẩm không có trong đơn hàng này");
        }
        
        // 5. Validate: User chưa đánh giá sản phẩm này trong đơn hàng này
        if (reviewRepository.existsByUserIdAndProductIdAndOrderId(
            userId, request.getProductId(), request.getOrderId()
        )) {
            throw new RuntimeException("Bạn đã đánh giá sản phẩm này rồi");
        }
        
        // 6. Validate: Product phải tồn tại
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        
        // 7. Validate: User phải tồn tại
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        
        // 8. Tạo Review
        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setOrder(order);
        review.setRating(request.getRating());
        review.setComment(request.getComment());
        
        // 9. Thêm ảnh nếu có
        if (request.getImageUrls() != null && !request.getImageUrls().isEmpty()) {
            for (int i = 0; i < request.getImageUrls().size(); i++) {
                ReviewImage image = new ReviewImage();
                image.setImageUrl(request.getImageUrls().get(i));
                image.setDisplayOrder(i);
                review.addImage(image);
            }
        }
        
        return reviewRepository.save(review);
    }
    
    /**
     * Lấy tất cả đánh giá của sản phẩm
     */
    public List<Review> getProductReviews(Integer productId) {
        return reviewRepository.findByProductIdOrderByCreatedAtDesc(productId);
    }
    
    /**
     * Lấy thống kê đánh giá của sản phẩm
     */
    public Map<String, Object> getProductReviewStats(Integer productId) {
        Map<String, Object> stats = new HashMap<>();
        
        // Trung bình rating
        Double avgRating = reviewRepository.getAverageRatingByProductId(productId);
        stats.put("averageRating", avgRating != null ? avgRating : 0.0);
        
        // Tổng số đánh giá
        Long totalReviews = reviewRepository.countByProductId(productId);
        stats.put("totalReviews", totalReviews);
        
        // Phân bố rating (số lượng mỗi loại sao)
        List<Object[]> distribution = reviewRepository.getRatingDistributionByProductId(productId);
        Map<Integer, Long> ratingDistribution = new HashMap<>();
        
        // Initialize với 0
        for (int i = 1; i <= 5; i++) {
            ratingDistribution.put(i, 0L);
        }
        
        // Fill data
        for (Object[] row : distribution) {
            Integer rating = (Integer) row[0];
            Long count = (Long) row[1];
            ratingDistribution.put(rating, count);
        }
        
        stats.put("ratingDistribution", ratingDistribution);
        
        return stats;
    }
    
    /**
     * Kiểm tra user có thể đánh giá sản phẩm trong đơn hàng không
     */
    public boolean canUserReview(Integer userId, Integer productId, Integer orderId) {
        // Lấy order
        Optional<Order> orderOpt = orderRepository.findById(orderId);
        if (orderOpt.isEmpty()) return false;
        
        Order order = orderOpt.get();
        
        // Kiểm tra các điều kiện
        boolean isOwner = order.getUser().getId().equals(userId);
        boolean isReviewableStatus = REVIEWABLE_STATUSES.contains(order.getStatus());
        boolean hasProduct = order.getOrderItems().stream()
            .anyMatch(item -> item.getProduct().getId().equals(productId));
        boolean notReviewedYet = !reviewRepository.existsByUserIdAndProductIdAndOrderId(
            userId, productId, orderId
        );
        
        return isOwner && isReviewableStatus && hasProduct && notReviewedYet;
    }
    
    /**
     * Lấy đánh giá của user
     */
    public List<Review> getUserReviews(Integer userId) {
        return reviewRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }
}
