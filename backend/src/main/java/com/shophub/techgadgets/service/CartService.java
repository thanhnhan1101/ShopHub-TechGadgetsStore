package com.shophub.techgadgets.service;

import com.shophub.techgadgets.entity.CartItem;
import com.shophub.techgadgets.entity.Product;
import com.shophub.techgadgets.entity.User;
import com.shophub.techgadgets.repository.CartItemRepository;
import com.shophub.techgadgets.repository.ProductRepository;
import com.shophub.techgadgets.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartService(CartItemRepository cartItemRepository,
                      UserRepository userRepository,
                      ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public List<CartItem> getCartByUserId(Integer userId) {
        return cartItemRepository.findByUserId(userId);
    }

    public CartItem addToCart(CartItem cartItem) {
        if (cartItem.getUser() == null || cartItem.getProduct() == null) {
            throw new RuntimeException("User and Product are required");
        }

        Integer userId = cartItem.getUser().getId();
        Integer productId = cartItem.getProduct().getId();

        // Check if item already exists in cart
        return cartItemRepository.findByUserIdAndProductId(userId, productId)
                .map(existing -> {
                    // Update quantity
                    existing.setQuantity(existing.getQuantity() + cartItem.getQuantity());
                    return cartItemRepository.save(existing);
                })
                .orElseGet(() -> {
                    // Add new item
                    User user = userRepository.findById(userId)
                            .orElseThrow(() -> new RuntimeException("User not found"));
                    Product product = productRepository.findById(productId)
                            .orElseThrow(() -> new RuntimeException("Product not found"));

                    cartItem.setUser(user);
                    cartItem.setProduct(product);
                    return cartItemRepository.save(cartItem);
                });
    }

    public CartItem updateCartItem(Integer id, Integer quantity) {
        CartItem cartItem = cartItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        cartItem.setQuantity(quantity);
        return cartItemRepository.save(cartItem);
    }

    public void removeFromCart(Integer id) {
        if (!cartItemRepository.existsById(id)) {
            throw new RuntimeException("Cart item not found");
        }
        cartItemRepository.deleteById(id);
    }

    public void clearCart(Integer userId) {
        List<CartItem> cartItems = cartItemRepository.findByUserId(userId);
        cartItemRepository.deleteAll(cartItems);
    }
}
