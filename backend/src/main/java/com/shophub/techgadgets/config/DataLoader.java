package com.shophub.techgadgets.config;

import com.shophub.techgadgets.entity.Category;
import com.shophub.techgadgets.entity.Product;
import com.shophub.techgadgets.entity.User;
import com.shophub.techgadgets.repository.CategoryRepository;
import com.shophub.techgadgets.repository.ProductRepository;
import com.shophub.techgadgets.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class DataLoader implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public DataLoader(CategoryRepository categoryRepository, 
                     ProductRepository productRepository,
                     UserRepository userRepository) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Load sample admin user
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setFullName("Admin User");
            admin.setEmail("admin@shophub.com");
            admin.setPasswordHash("$2a$10$SAMPLE_HASH"); // In production, use BCrypt
            admin.setRole("ADMIN");
            userRepository.save(admin);

            User customer = new User();
            customer.setFullName("John Doe");
            customer.setEmail("customer@example.com");
            customer.setPasswordHash("$2a$10$SAMPLE_HASH");
            customer.setRole("CUSTOMER");
            userRepository.save(customer);
        }

        // Load sample categories
        if (categoryRepository.count() == 0) {
            Category smartphones = categoryRepository.save(
                new Category("Smartphones", "Latest smartphones and mobile devices")
            );
            Category laptops = categoryRepository.save(
                new Category("Laptops", "Laptops and notebook computers")
            );
            Category headphones = categoryRepository.save(
                new Category("Headphones", "Wireless and wired headphones")
            );
            Category smartwatches = categoryRepository.save(
                new Category("Smartwatches", "Fitness trackers and smartwatches")
            );
            Category cameras = categoryRepository.save(
                new Category("Cameras", "Action cameras and photography equipment")
            );

            // Load sample products
            if (productRepository.count() == 0) {
                Product p1 = new Product();
                p1.setCategory(headphones);
                p1.setName("Wireless Noise-Cancelling Headphones");
                p1.setDescription("Premium over-ear headphones with active noise cancellation");
                p1.setPrice(new BigDecimal("129.99"));
                p1.setStock(50);
                productRepository.save(p1);

                Product p2 = new Product();
                p2.setCategory(smartwatches);
                p2.setName("Smart Fitness Watch");
                p2.setDescription("Fitness tracker with heart rate sensor and GPS");
                p2.setPrice(new BigDecimal("89.50"));
                p2.setStock(120);
                productRepository.save(p2);

                Product p3 = new Product();
                p3.setCategory(cameras);
                p3.setName("4K Action Camera");
                p3.setDescription("Waterproof action camera with 4K recording");
                p3.setPrice(new BigDecimal("199.99"));
                p3.setStock(30);
                productRepository.save(p3);

                Product p4 = new Product();
                p4.setCategory(smartphones);
                p4.setName("Pro Smartphone 128GB");
                p4.setDescription("Latest flagship smartphone with AI camera");
                p4.setPrice(new BigDecimal("899.00"));
                p4.setStock(25);
                productRepository.save(p4);

                Product p5 = new Product();
                p5.setCategory(laptops);
                p5.setName("UltraBook Pro 15\"");
                p5.setDescription("Thin and light laptop with 16GB RAM");
                p5.setPrice(new BigDecimal("1299.99"));
                p5.setStock(15);
                productRepository.save(p5);
            }
        }
    }
}
