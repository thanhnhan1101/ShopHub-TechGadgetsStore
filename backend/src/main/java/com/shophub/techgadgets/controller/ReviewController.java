package com.shophub.techgadgets.controller;

import com.shophub.techgadgets.dto.ReviewRequest;
import com.shophub.techgadgets.entity.Review;
import com.shophub.techgadgets.security.JwtTokenProvider;
import com.shophub.techgadgets.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@CrossOrigin(origins = "http://localhost:5173")
public class ReviewController {
    
    @Autowired
    private ReviewService reviewService;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    /**
     * Tạo đánh giá mới
     */
    @PostMapping
    public ResponseEntity<?> createReview(
        @RequestHeader("Authorization") String token,
        @Valid @RequestBody ReviewRequest request
    ) {
        try {
            // Extract userId from JWT token
            String jwt = token.replace("Bearer ", "");
            String username = jwtTokenProvider.getUsernameFromToken(jwt);
            Integer userId = jwtTokenProvider.getUserIdFromToken(jwt);
            
            // Check if userId is null (old token format)
            if (userId == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token không hợp lệ. Vui lòng đăng nhập lại!");
                return ResponseEntity.status(401).body(error);
            }
            
            Review review = reviewService.createReview(userId, request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "Cảm ơn bạn đã đánh giá!");
            response.put("review", review);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Lấy tất cả đánh giá của sản phẩm
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<Review>> getProductReviews(@PathVariable Integer productId) {
        List<Review> reviews = reviewService.getProductReviews(productId);
        return ResponseEntity.ok(reviews);
    }
    
    /**
     * Lấy thống kê đánh giá của sản phẩm
     */
    @GetMapping("/product/{productId}/stats")
    public ResponseEntity<Map<String, Object>> getProductReviewStats(@PathVariable Integer productId) {
        Map<String, Object> stats = reviewService.getProductReviewStats(productId);
        return ResponseEntity.ok(stats);
    }
    
    /**
     * Kiểm tra user có thể đánh giá không
     */
    @GetMapping("/can-review")
    public ResponseEntity<?> canReview(
        @RequestHeader("Authorization") String token,
        @RequestParam Integer productId,
        @RequestParam Integer orderId
    ) {
        try {
            String jwt = token.replace("Bearer ", "");
            Integer userId = jwtTokenProvider.getUserIdFromToken(jwt);
            
            if (userId == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token không hợp lệ. Vui lòng đăng nhập lại!");
                return ResponseEntity.status(401).body(error);
            }
            
            boolean canReview = reviewService.canUserReview(userId, productId, orderId);
            
            Map<String, Boolean> response = new HashMap<>();
            response.put("canReview", canReview);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
    
    /**
     * Lấy đánh giá của user
     */
    @GetMapping("/my-reviews")
    public ResponseEntity<?> getMyReviews(@RequestHeader("Authorization") String token) {
        try {
            String jwt = token.replace("Bearer ", "");
            Integer userId = jwtTokenProvider.getUserIdFromToken(jwt);
            
            if (userId == null) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Token không hợp lệ. Vui lòng đăng nhập lại!");
                return ResponseEntity.status(401).body(error);
            }
            
            List<Review> reviews = reviewService.getUserReviews(userId);
            return ResponseEntity.ok(reviews);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}
