package com.shophub.techgadgets.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryService {

    private final Cloudinary cloudinary;

    public CloudinaryService(Cloudinary cloudinary) {
        this.cloudinary = cloudinary;
    }

    /**
     * Upload ảnh lên Cloudinary
     * @param file File ảnh từ frontend
     * @return URL của ảnh trên Cloudinary
     */
    public String uploadImage(MultipartFile file) throws IOException {
        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File không được để trống");
        }

        // Kiểm tra loại file
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("File phải là ảnh (PNG, JPG, GIF)");
        }

        // Kiểm tra kích thước (max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File quá lớn (max 5MB)");
        }

        // Upload lên Cloudinary với options
        Map<String, Object> options = ObjectUtils.asMap(
            "folder", "shophub/products",           // Lưu vào folder products
            "resource_type", "image",               // Loại resource
            "width", 800,                           // Resize width
            "height", 800,                          // Resize height
            "crop", "limit",                        // Giữ tỷ lệ, không cắt
            "quality", "auto"                       // Tự động optimize
        );

        Map uploadResult = cloudinary.uploader().upload(file.getBytes(), options);
        
        // Lấy secure URL (HTTPS)
        return (String) uploadResult.get("secure_url");
    }

    /**
     * Xóa ảnh từ Cloudinary (dùng khi xóa sản phẩm)
     * @param imageUrl URL ảnh cần xóa
     */
    public void deleteImage(String imageUrl) throws IOException {
        if (imageUrl == null || !imageUrl.contains("cloudinary.com")) {
            return; // Không phải ảnh từ Cloudinary
        }

        // Extract public_id từ URL
        // URL dạng: https://res.cloudinary.com/xxx/image/upload/v123/shophub/products/abc.jpg
        String publicId = extractPublicId(imageUrl);
        
        if (publicId != null) {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        }
    }

    private String extractPublicId(String imageUrl) {
        try {
            // Lấy phần sau "/upload/"
            int uploadIndex = imageUrl.indexOf("/upload/");
            if (uploadIndex == -1) return null;

            String afterUpload = imageUrl.substring(uploadIndex + 8);
            
            // Bỏ version number (vXXX/)
            if (afterUpload.startsWith("v")) {
                int slashIndex = afterUpload.indexOf("/");
                if (slashIndex != -1) {
                    afterUpload = afterUpload.substring(slashIndex + 1);
                }
            }

            // Bỏ extension (.jpg, .png...)
            int dotIndex = afterUpload.lastIndexOf(".");
            if (dotIndex != -1) {
                afterUpload = afterUpload.substring(0, dotIndex);
            }

            return afterUpload;
        } catch (Exception e) {
            return null;
        }
    }
}
