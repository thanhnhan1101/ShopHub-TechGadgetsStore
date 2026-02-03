package com.shophub.techgadgets.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "REVIEW_IMAGES", indexes = {
    @Index(name = "IDX_REVIEW_IMAGES_REVIEW", columnList = "REVIEW_ID")
})
public class ReviewImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "REVIEW_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_REVIEW_IMAGES_REVIEW"))
    @JsonIgnore
    private Review review;

    @Column(name = "IMAGE_URL", columnDefinition = "NVARCHAR(MAX)", nullable = false)
    private String imageUrl;

    @Column(name = "DISPLAY_ORDER", nullable = false)
    private Integer displayOrder = 0;

    // Constructors
    public ReviewImage() {}

    public ReviewImage(Review review, String imageUrl, Integer displayOrder) {
        this.review = review;
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Review getReview() {
        return review;
    }

    public void setReview(Review review) {
        this.review = review;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getDisplayOrder() {
        return displayOrder;
    }

    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
}
