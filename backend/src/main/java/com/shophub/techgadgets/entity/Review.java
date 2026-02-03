package com.shophub.techgadgets.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "REVIEWS", indexes = {
    @Index(name = "IDX_REVIEWS_PRODUCT", columnList = "PRODUCT_ID"),
    @Index(name = "IDX_REVIEWS_USER", columnList = "USER_ID"),
    @Index(name = "IDX_REVIEWS_ORDER", columnList = "ORDER_ID"),
    @Index(name = "IDX_REVIEWS_RATING", columnList = "RATING")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_REVIEWS_PRODUCT"))
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "reviews", "images"})
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "USER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_REVIEWS_USER"))
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "password", "orders", "cartItems"})
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_REVIEWS_ORDER"))
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "orderItems", "user"})
    private Order order;

    @Column(name = "RATING", nullable = false)
    @Min(1)
    @Max(5)
    private Integer rating;

    @Column(name = "COMMENT", columnDefinition = "NVARCHAR(MAX)")
    private String comment;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("displayOrder ASC")
    private List<ReviewImage> images = new ArrayList<>();

    @Column(name = "CREATED_AT", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "UPDATED_AT")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Constructors
    public Review() {}

    public Review(Product product, User user, Order order, Integer rating, String comment) {
        this.product = product;
        this.user = user;
        this.order = order;
        this.rating = rating;
        this.comment = comment;
    }

    // Helper methods
    public void addImage(ReviewImage image) {
        images.add(image);
        image.setReview(this);
    }

    public void removeImage(ReviewImage image) {
        images.remove(image);
        image.setReview(null);
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
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

    public List<ReviewImage> getImages() {
        return images;
    }

    public void setImages(List<ReviewImage> images) {
        this.images = images;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
