package com.shophub.techgadgets.controller;

import com.shophub.techgadgets.service.CloudinaryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    /**
     * Upload nhiều ảnh sản phẩm cùng lúc
     * @param files Mảng các file ảnh từ frontend (multipart/form-data)
     * @return JSON với danh sách imageUrls từ Cloudinary
     */
    @PostMapping("/images")
    public ResponseEntity<?> uploadMultipleImages(@RequestParam("files") MultipartFile[] files) {
        try {
            if (files == null || files.length == 0) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Vui lòng chọn ít nhất 1 ảnh");
                return ResponseEntity.badRequest().body(error);
            }

            // Giới hạn số lượng ảnh (tối đa 10 ảnh)
            if (files.length > 10) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Chỉ được upload tối đa 10 ảnh");
                return ResponseEntity.badRequest().body(error);
            }

            List<String> imageUrls = new ArrayList<>();
            List<String> errors = new ArrayList<>();

            for (int i = 0; i < files.length; i++) {
                try {
                    String imageUrl = cloudinaryService.uploadImage(files[i]);
                    imageUrls.add(imageUrl);
                } catch (Exception e) {
                    errors.add("File " + (i + 1) + ": " + e.getMessage());
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("imageUrls", imageUrls);
            response.put("message", "Upload thành công " + imageUrls.size() + "/" + files.length + " ảnh");
            
            if (!errors.isEmpty()) {
                response.put("errors", errors);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Lỗi khi upload ảnh: " + e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
