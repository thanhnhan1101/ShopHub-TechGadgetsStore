package com.shophub.techgadgets.dto;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * DTO để nhận dữ liệu sản phẩm từ frontend với nhiều ảnh
 */
public class ProductRequest {
    
    private Integer id;
    private Integer categoryId;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private Boolean isActive = true;
    
    // Danh sách URL ảnh (từ Cloudinary sau khi upload)
    private List<String> imageUrls = new ArrayList<>();
    
    // Giữ lại imageUrl đơn cho backward compatibility
    private String imageUrl;

    // Constructors
    public ProductRequest() {}

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
