package com.shophub.techgadgets.dto;

import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO để nhận dữ liệu đánh giá từ frontend
 */
public class ReviewRequest {
    
    @NotNull(message = "Product ID không được null")
    private Integer productId;
    
    @NotNull(message = "Order ID không được null")
    private Integer orderId;
    
    @NotNull(message = "Rating không được null")
    @Min(value = 1, message = "Rating tối thiểu là 1")
    @Max(value = 5, message = "Rating tối đa là 5")
    private Integer rating;
    
    @Size(max = 1000, message = "Comment không quá 1000 ký tự")
    private String comment;
    
    private List<String> imageUrls = new ArrayList<>();

    // Constructors
    public ReviewRequest() {}

    public ReviewRequest(Integer productId, Integer orderId, Integer rating, String comment) {
        this.productId = productId;
        this.orderId = orderId;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters and Setters
    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public Integer getRating() {
        return rating;
    }

    public void setRating(Integer rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }
}
