package com.shophub.techgadgets.controller;

import com.shophub.techgadgets.entity.User;
import com.shophub.techgadgets.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5174"})
public class UserController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * READ - Lấy danh sách tất cả người dùng
     * Chỉ ADMIN mới có quyền xem danh sách user
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * READ - Lấy thông tin 1 người dùng theo ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Integer id) {
        return userRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * CREATE - Tạo người dùng mới (chỉ ADMIN)
     * Body: { "email": "...", "password": "...", "fullName": "...", "phone": "...", "role": "CUSTOMER" }
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createUser(@RequestBody User user) {
        // Kiểm tra email đã tồn tại chưa
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            return ResponseEntity.badRequest().body("Email đã tồn tại!");
        }

        // Mã hóa password
        user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));
        
        // Đảm bảo user được tạo ở trạng thái active
        user.setIsActive(true);
        
        User savedUser = userRepository.save(user);
        
        // Xóa password khỏi response
        savedUser.setPasswordHash(null);
        
        return ResponseEntity.ok(savedUser);
    }

    /**
     * UPDATE - Cập nhật thông tin người dùng
     * Body: { "fullName": "...", "phone": "...", "role": "...", "isActive": true }
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateUser(@PathVariable Integer id, @RequestBody User userDetails) {
        return userRepository.findById(id)
                .map(user -> {
                    // Cập nhật các trường được phép
                    if (userDetails.getFullName() != null) {
                        user.setFullName(userDetails.getFullName());
                    }
                    if (userDetails.getPhone() != null) {
                        user.setPhone(userDetails.getPhone());
                    }
                    if (userDetails.getRole() != null) {
                        user.setRole(userDetails.getRole());
                    }
                    if (userDetails.getIsActive() != null) {
                        user.setIsActive(userDetails.getIsActive());
                    }
                    
                    // Nếu có password mới, mã hóa và cập nhật
                    if (userDetails.getPasswordHash() != null && !userDetails.getPasswordHash().isEmpty()) {
                        user.setPasswordHash(passwordEncoder.encode(userDetails.getPasswordHash()));
                    }
                    
                    User updatedUser = userRepository.save(user);
                    
                    // Xóa password khỏi response
                    updatedUser.setPasswordHash(null);
                    
                    return ResponseEntity.ok(updatedUser);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * DELETE - Xóa người dùng (soft delete - set isActive = false)
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteUser(@PathVariable Integer id) {
        return userRepository.findById(id)
                .map(user -> {
                    // Không cho phép xóa ADMIN
                    if ("ADMIN".equals(user.getRole())) {
                        return ResponseEntity.badRequest().body("Không thể xóa tài khoản ADMIN!");
                    }
                    
                    // Soft delete - chỉ set isActive = false
                    user.setIsActive(false);
                    userRepository.save(user);
                    
                    return ResponseEntity.ok().body("Đã vô hiệu hóa tài khoản!");
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Hard Delete - Xóa vĩnh viễn (dùng cẩn thận)
     */
    @DeleteMapping("/{id}/permanent")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> permanentDeleteUser(@PathVariable Integer id) {
        return userRepository.findById(id)
                .map(user -> {
                    if ("ADMIN".equals(user.getRole())) {
                        return ResponseEntity.badRequest().body("Không thể xóa tài khoản ADMIN!");
                    }
                    
                    userRepository.deleteById(id);
                    return ResponseEntity.ok().body("Đã xóa vĩnh viễn tài khoản!");
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
