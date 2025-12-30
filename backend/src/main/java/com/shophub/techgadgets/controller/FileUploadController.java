package com.shophub.techgadgets.controller;

import com.shophub.techgadgets.service.CloudinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "http://localhost:5173")
public class FileUploadController {

    private final CloudinaryService cloudinaryService;

    public FileUploadController(CloudinaryService cloudinaryService) {
        this.cloudinaryService = cloudinaryService;
    }

    /**
     * Upload ảnh sản phẩm
     * @param file File ảnh từ frontend (multipart/form-data)
     * @return JSON với imageUrl từ Cloudinary
     */
    @PostMapping("/image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String imageUrl = cloudinaryService.uploadImage(file);
            
            Map<String, String> response = new HashMap<>();
            response.put("imageUrl", imageUrl);
            response.put("message", "Upload ảnh thành công!");
            
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Validation errors
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        } catch (Exception e) {
            // Server errors
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi khi upload ảnh: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
