package com.shophub.techgadgets.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

@Entity
@Table(name = "PRODUCT_IMAGES", indexes = {
    @Index(name = "IDX_PRODUCT_IMAGES_PRODUCT", columnList = "PRODUCT_ID")
})
public class ProductImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_PRODUCT_IMAGES_PRODUCT"))
    @JsonIgnore
    private Product product;

    @Column(name = "IMAGE_URL", columnDefinition = "NVARCHAR(MAX)", nullable = false)
    private String imageUrl;

    @Column(name = "DISPLAY_ORDER", nullable = false)
    private Integer displayOrder = 0;

    @Column(name = "IS_PRIMARY", nullable = false)
    private Boolean isPrimary = false;

    // Constructors
    public ProductImage() {}

    public ProductImage(Product product, String imageUrl, Integer displayOrder, Boolean isPrimary) {
        this.product = product;
        this.imageUrl = imageUrl;
        this.displayOrder = displayOrder;
        this.isPrimary = isPrimary;
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

    public Boolean getIsPrimary() {
        return isPrimary;
    }

    public void setIsPrimary(Boolean isPrimary) {
        this.isPrimary = isPrimary;
    }
}
