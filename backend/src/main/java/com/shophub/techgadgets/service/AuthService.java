package com.shophub.techgadgets.service;

import com.shophub.techgadgets.dto.AuthResponse;
import com.shophub.techgadgets.dto.LoginRequest;
import com.shophub.techgadgets.dto.RegisterRequest;
import com.shophub.techgadgets.entity.User;
import com.shophub.techgadgets.repository.UserRepository;
import com.shophub.techgadgets.security.JwtTokenProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider tokenProvider;

    public AuthService(UserRepository userRepository,
                      PasswordEncoder passwordEncoder,
                      JwtTokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenProvider = tokenProvider;
    }

    public AuthResponse register(RegisterRequest request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists!");
        }

        // Create new user
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullName());
        user.setPhone(request.getPhone());
        user.setRole("CUSTOMER");
        user.setIsActive(true);

        User savedUser = userRepository.save(user);

        // Generate JWT token
        String token = tokenProvider.generateToken(savedUser.getEmail(), savedUser.getRole());

        return new AuthResponse(
                savedUser.getId(),
                token,
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getPhone(),
                savedUser.getRole()
        );
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid email or password!"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid email or password!");
        }

        if (!user.getIsActive()) {
            throw new RuntimeException("Account is disabled!");
        }

        // Generate JWT token
        String token = tokenProvider.generateToken(user.getEmail(), user.getRole());

        return new AuthResponse(
                user.getId(),
                token,
                user.getEmail(),
                user.getFullName(),
                user.getPhone(),
                user.getRole()
        );
    }

    public User getCurrentUser(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
