package com.shophub.techgadgets.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "ORDER_ITEMS", indexes = {
    @Index(name = "IDX_ORDER_ITEMS_ORDER", columnList = "ORDER_ID"),
    @Index(name = "IDX_ORDER_ITEMS_PRODUCT", columnList = "PRODUCT_ID")
})
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ORDER_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_ORDER_ITEMS_ORDER"))
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PRODUCT_ID", nullable = false, foreignKey = @ForeignKey(name = "FK_ORDER_ITEMS_PRODUCT"))
    private Product product;

    @Column(name = "PRODUCT_NAME", nullable = false, length = 200)
    private String productName;

    @Column(name = "PRICE", nullable = false, precision = 18, scale = 2)
    private BigDecimal price;

    @Column(name = "QUANTITY", nullable = false)
    private Integer quantity;

    // Constructors
    public OrderItem() {}

    public OrderItem(Order order, Product product, String productName, BigDecimal price, Integer quantity) {
        this.order = order;
        this.product = product;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
    }

    // Helper method to calculate subtotal
    public BigDecimal getSubtotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
