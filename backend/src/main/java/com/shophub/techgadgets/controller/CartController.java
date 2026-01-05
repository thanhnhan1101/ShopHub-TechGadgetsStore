package com.shophub.techgadgets.controller;

import com.shophub.techgadgets.entity.CartItem;
import com.shophub.techgadgets.repository.CartItemRepository;
import com.shophub.techgadgets.repository.ProductRepository;
import com.shophub.techgadgets.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = "http://localhost:5173")
public class CartController {

    private final CartItemRepository cartItemRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public CartController(CartItemRepository cartItemRepository, 
                         UserRepository userRepository, 
                         ProductRepository productRepository) {
        this.cartItemRepository = cartItemRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    @GetMapping("/{userId}")
    public List<CartItem> getCartByUserId(@PathVariable Integer userId) {
        return cartItemRepository.findByUserId(userId);
    }

    @PostMapping
    public ResponseEntity<CartItem> addToCart(@RequestBody CartItem cartItem) {
        if (cartItem.getUser() == null || cartItem.getProduct() == null) {
            return ResponseEntity.badRequest().build();
        }

        // Check if item already exists in cart
        return cartItemRepository.findByUserIdAndProductId(
                cartItem.getUser().getId(), 
                cartItem.getProduct().getId()
        ).map(existing -> {
            // Update quantity
            existing.setQuantity(existing.getQuantity() + cartItem.getQuantity());
            return ResponseEntity.ok(cartItemRepository.save(existing));
        }).orElseGet(() -> {
            // Add new item
            return userRepository.findById(cartItem.getUser().getId())
                    .flatMap(user -> productRepository.findById(cartItem.getProduct().getId())
                            .map(product -> {
                                cartItem.setUser(user);
                                cartItem.setProduct(product);
                                return ResponseEntity.ok(cartItemRepository.save(cartItem));
                            }))
                    .orElse(ResponseEntity.badRequest().build());
        });
    }

    @PutMapping("/{id}")
    public ResponseEntity<CartItem> updateCartItem(@PathVariable Integer id, @RequestBody CartItem cartItemDetails) {
        return cartItemRepository.findById(id)
                .map(cartItem -> {
                    cartItem.setQuantity(cartItemDetails.getQuantity());
                    return ResponseEntity.ok(cartItemRepository.save(cartItem));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> removeFromCart(@PathVariable Integer id) {
        if (cartItemRepository.existsById(id)) {
            cartItemRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/user/{userId}")
    @Transactional
    public ResponseEntity<Void> clearCart(@PathVariable Integer userId) {
        try {
            cartItemRepository.deleteByUserId(userId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }
}
