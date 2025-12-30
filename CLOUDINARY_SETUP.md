# 📸 Hướng Dẫn Setup Cloudinary

## Bước 1: Tạo Tài Khoản Cloudinary (FREE)

1. Truy cập: https://cloudinary.com/users/register/free
2. Đăng ký với email hoặc GitHub/Google
3. Xác nhận email

## Bước 2: Lấy Thông Tin Credentials

1. Đăng nhập vào: https://console.cloudinary.com/
2. Vào **Dashboard** (trang đầu tiên sau khi đăng nhập)
3. Tìm phần **Account Details**:
   ```
   Cloud name: your-cloud-name
   API Key: 123456789012345
   API Secret: your-api-secret
   ```

## Bước 3: Cấu Hình Backend

1. Mở file `backend/src/main/resources/application.properties`
2. Thay thế các giá trị:
   ```properties
   cloudinary.cloud-name=YOUR_CLOUD_NAME      # Ví dụ: dtj1abc123
   cloudinary.api-key=YOUR_API_KEY            # Ví dụ: 123456789012345
   cloudinary.api-secret=YOUR_API_SECRET      # Ví dụ: abc123xyz789
   ```

## Bước 4: Restart Backend

```bash
# Dừng terminal "Run: ShopHubApplication"
# Chạy lại backend
```

## Bước 5: Test Upload

1. Vào trang Product Management
2. Click "Thêm Sản Phẩm"
3. Upload 1 ảnh bất kỳ
4. Kiểm tra console → Sẽ thấy URL Cloudinary:
   ```
   ✅ Upload thành công: https://res.cloudinary.com/dtj1abc123/image/upload/v123/shophub/products/abc.jpg
   ```

## 🎯 Tính Năng Đã Implement

### Backend:
- ✅ **CloudinaryConfig**: Config Cloudinary SDK
- ✅ **CloudinaryService**: 
  - Upload ảnh với tự động resize (max 800x800)
  - Optimize quality tự động
  - Delete ảnh khi xóa product
- ✅ **FileUploadController**: 
  - Endpoint `/api/upload/image`
  - Nhận multipart/form-data
  - Validation file size (max 5MB) và type

### Frontend:
- ✅ **Upload UI**: Tab chọn File hoặc URL
- ✅ **Preview**: Xem trước ảnh ngay lập tức
- ✅ **API Integration**: Gửi file qua FormData
- ✅ **Error Handling**: Hiển thị lỗi rõ ràng

## 📦 Free Tier Limits

- **Storage**: 25 GB
- **Bandwidth**: 25 GB/tháng
- **Transformations**: 25,000/tháng
- **API Requests**: Unlimited

→ **Đủ dùng cho project CV!**

## 🔐 Bảo Mật

**⚠️ LƯU Ý**: Đừng commit `application.properties` với API credentials lên GitHub!

### Cách bảo mật:

1. Tạo file `.env` hoặc `application-local.properties`
2. Add vào `.gitignore`:
   ```
   application-local.properties
   .env
   ```
3. Khi deploy, dùng Environment Variables

## 🚀 Deploy

### Khi deploy lên Heroku/Railway/Render:

Thêm Environment Variables:
```
CLOUDINARY_CLOUD_NAME=your-cloud-name
CLOUDINARY_API_KEY=your-api-key
CLOUDINARY_API_SECRET=your-api-secret
```

Cập nhật `application.properties`:
```properties
cloudinary.cloud-name=${CLOUDINARY_CLOUD_NAME}
cloudinary.api-key=${CLOUDINARY_API_KEY}
cloudinary.api-secret=${CLOUDINARY_API_SECRET}
```

## 🎨 Customize

### Thay đổi kích thước ảnh:

Trong `CloudinaryService.java`:
```java
"transformation", ObjectUtils.asMap(
    "width", 1200,        // Tăng kích thước
    "height", 1200,
    "crop", "limit"
)
```

### Thêm watermark:

```java
"transformation", ObjectUtils.asMap(
    "width", 800,
    "height", 800,
    "overlay", "your-logo",
    "gravity", "south_east"
)
```

## 📚 Resources

- [Cloudinary Docs](https://cloudinary.com/documentation)
- [Java SDK Guide](https://cloudinary.com/documentation/java_integration)
- [Image Transformations](https://cloudinary.com/documentation/image_transformations)

---

**Hoàn thành! Bây giờ bạn có hệ thống upload ảnh professional cho CV** 🎉
