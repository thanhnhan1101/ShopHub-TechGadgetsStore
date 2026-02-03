# 🌟 Hệ Thống Đánh Giá Sản Phẩm - Hướng Dẫn Triển Khai

## 📋 Tổng Quan
Tính năng đánh giá sản phẩm cho phép khách hàng đánh giá sản phẩm sau khi đơn hàng đã **THANH TOÁN** (PAID status). Hệ thống hỗ trợ:
- ⭐ Rating 1-5 sao
- 💬 Bình luận văn bản
- 📸 Upload nhiều ảnh đánh giá
- 📊 Thống kê trung bình rating và phân bố sao
- 🔒 Validation để tránh đánh giá trùng lặp

---

## 🗂️ Cấu Trúc File

### Backend (Java Spring Boot)
```
backend/
├── create-review-tables.sql                    # SQL migration script
├── src/main/java/com/shophub/techgadgets/
    ├── entity/
    │   ├── Review.java                         # Entity đánh giá
    │   └── ReviewImage.java                    # Entity ảnh đánh giá
    ├── repository/
    │   └── ReviewRepository.java               # Repository với query methods
    ├── dto/
    │   └── ReviewRequest.java                  # DTO nhận dữ liệu từ frontend
    ├── service/
    │   └── ReviewService.java                  # Business logic
    └── controller/
        └── ReviewController.java               # REST API endpoints
```

### Frontend (Vue.js)
```
frontend/src/
├── components/
│   ├── ReviewForm.vue                          # Form đánh giá
│   └── ReviewList.vue                          # Danh sách đánh giá
├── services/
│   └── api.js                                  # Thêm review API methods
└── views/
    ├── Orders.vue                              # Thêm nút đánh giá
    └── ProductDetail.vue                       # Hiển thị đánh giá
```

---

## 🚀 Bước 1: Triển Khai Database

### 1.1. Chạy SQL Migration
```sql
-- File: backend/create-review-tables.sql
-- Tạo 2 bảng: REVIEWS và REVIEW_IMAGES
```

**Thực hiện:**
```bash
# Mở SQL Server Management Studio hoặc Azure Data Studio
# Kết nối tới database của bạn
# Chạy script: backend/create-review-tables.sql
```

### 1.2. Kiểm Tra Kết Quả
```sql
-- Kiểm tra bảng đã tạo
SELECT * FROM INFORMATION_SCHEMA.TABLES 
WHERE TABLE_NAME IN ('REVIEWS', 'REVIEW_IMAGES');

-- Kiểm tra indexes
SELECT * FROM sys.indexes 
WHERE object_id IN (OBJECT_ID('REVIEWS'), OBJECT_ID('REVIEW_IMAGES'));
```

---

## 🔧 Bước 2: Khởi Động Backend

### 2.1. Restart Spring Boot Application
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

### 2.2. Kiểm Tra Console Log
Tìm các dòng log:
```
Hibernate: create table reviews ...
Hibernate: create table review_images ...
```

### 2.3. Test API Endpoints
```bash
# 1. Tạo đánh giá (cần JWT token)
POST http://localhost:8080/api/reviews
Headers: Authorization: Bearer <your-jwt-token>
Body: {
  "productId": 1,
  "orderId": 1,
  "rating": 5,
  "comment": "Sản phẩm tuyệt vời!",
  "imageUrls": [
    "https://res.cloudinary.com/..."
  ]
}

# 2. Lấy đánh giá của sản phẩm
GET http://localhost:8080/api/reviews/product/1

# 3. Lấy thống kê đánh giá
GET http://localhost:8080/api/reviews/product/1/stats

# 4. Kiểm tra có thể đánh giá không
GET http://localhost:8080/api/reviews/can-review?productId=1&orderId=1
Headers: Authorization: Bearer <your-jwt-token>
```

---

## 🎨 Bước 3: Khởi Động Frontend

### 3.1. Install và Run
```bash
cd frontend
npm install
npm run dev
```

### 3.2. Test Giao Diện

#### A. Test trong Orders Page
1. Đăng nhập vào tài khoản
2. Truy cập: http://localhost:5173/orders
3. Tìm đơn hàng có trạng thái **"Đã thanh toán"**
4. Click nút **"⭐ Đánh giá"** bên dưới sản phẩm
5. Modal form đánh giá sẽ hiện ra

#### B. Test Form Đánh Giá
1. Chọn số sao (1-5)
2. Nhập nhận xét (tùy chọn)
3. Upload ảnh (tùy chọn, tối đa 5 ảnh)
4. Click **"Gửi đánh giá"**
5. Kiểm tra notification thành công

#### C. Test Hiển Thị Đánh Giá
1. Truy cập trang chi tiết sản phẩm đã đánh giá
2. Scroll xuống phần **"Đánh giá sản phẩm"**
3. Kiểm tra:
   - ⭐ Trung bình rating
   - 📊 Biểu đồ phân bố sao
   - 💬 Danh sách đánh giá
   - 📸 Ảnh đánh giá (click để phóng to)

---

## 🔍 Bước 4: Kiểm Tra Validation

### 4.1. Test Business Rules

#### Test 1: Chỉ review sau khi PAID
```
Scenario: Đơn hàng PENDING
Expected: Nút "Đánh giá" KHÔNG hiển thị
```

#### Test 2: Không review trùng lặp
```
Scenario: Đã đánh giá sản phẩm A trong đơn hàng #1
Action: Đánh giá lại sản phẩm A trong đơn hàng #1
Expected: Error "Bạn đã đánh giá sản phẩm này rồi"
```

#### Test 3: Chỉ review sản phẩm trong đơn hàng
```
Scenario: Đơn hàng #1 có sản phẩm A, B
Action: Đánh giá sản phẩm C qua API
Expected: Error "Sản phẩm không có trong đơn hàng này"
```

#### Test 4: Chỉ review đơn hàng của mình
```
Scenario: User A cố gắng review đơn hàng của User B
Expected: Error "Đơn hàng không thuộc về bạn"
```

---

## 📊 Bước 5: Kiểm Tra Data

### 5.1. Xem Reviews trong Database
```sql
-- Xem tất cả đánh giá
SELECT 
    r.ID,
    r.RATING,
    r.COMMENT,
    r.CREATED_AT,
    p.NAME as PRODUCT_NAME,
    u.FULL_NAME as USER_NAME,
    o.ID as ORDER_ID
FROM REVIEWS r
JOIN PRODUCTS p ON r.PRODUCT_ID = p.ID
JOIN USERS u ON r.USER_ID = u.ID
JOIN ORDERS o ON r.ORDER_ID = o.ID
ORDER BY r.CREATED_AT DESC;

-- Xem ảnh đánh giá
SELECT 
    ri.ID,
    ri.IMAGE_URL,
    ri.DISPLAY_ORDER,
    r.RATING,
    p.NAME as PRODUCT_NAME
FROM REVIEW_IMAGES ri
JOIN REVIEWS r ON ri.REVIEW_ID = r.ID
JOIN PRODUCTS p ON r.PRODUCT_ID = p.ID;

-- Thống kê rating trung bình
SELECT 
    p.NAME as PRODUCT_NAME,
    AVG(CAST(r.RATING AS FLOAT)) as AVG_RATING,
    COUNT(r.ID) as TOTAL_REVIEWS
FROM PRODUCTS p
LEFT JOIN REVIEWS r ON p.ID = r.PRODUCT_ID
GROUP BY p.ID, p.NAME
ORDER BY AVG_RATING DESC;
```

---

## 🎯 Workflow Đánh Giá

### Quy Trình Hoàn Chỉnh

```
1. 🛒 User đặt hàng
   └─> Order status: PENDING

2. 💳 User thanh toán
   └─> Admin cập nhật order status → PAID
   └─> ✅ Nút "Đánh giá" xuất hiện trong Orders page

3. ⭐ User click "Đánh giá"
   └─> Modal ReviewForm hiện ra
   └─> User chọn sao, nhập comment, upload ảnh
   └─> Submit

4. 🔍 Backend validate:
   ├─> Order phải tồn tại
   ├─> Order phải thuộc về user
   ├─> Order status phải PAID/SHIPPING/DELIVERED
   ├─> Product phải có trong order
   └─> Chưa đánh giá trước đó

5. ✅ Lưu review vào database
   └─> Review + ReviewImages được tạo
   └─> Frontend cập nhật UI (ẩn nút đánh giá)

6. 👀 Hiển thị review
   └─> ProductDetail.vue show ReviewList component
   └─> Tính toán stats (avg rating, distribution)
   └─> Render danh sách reviews với ảnh
```

---

## 🔐 Security & Validation

### Backend Validations
```java
// 1. JWT Authentication (trong controller)
String jwt = token.replace("Bearer ", "");
Integer userId = jwtTokenProvider.getUserIdFromToken(jwt);

// 2. Order ownership
if (!order.getUser().getId().equals(userId)) {
    throw new RuntimeException("Đơn hàng không thuộc về bạn");
}

// 3. Reviewable status
if (!REVIEWABLE_STATUSES.contains(order.getStatus())) {
    throw new RuntimeException("Chỉ có thể đánh giá sau khi đơn hàng đã thanh toán");
}

// 4. Product in order
boolean productInOrder = order.getOrderItems().stream()
    .anyMatch(item -> item.getProduct().getId().equals(productId));

// 5. No duplicate review
if (reviewRepository.existsByUserIdAndProductIdAndOrderId(...)) {
    throw new RuntimeException("Bạn đã đánh giá sản phẩm này rồi");
}
```

### Frontend Validations
```javascript
// 1. Rating required
if (!rating.value) {
    showNotification('Vui lòng chọn số sao đánh giá', 'error');
    return;
}

// 2. Comment length
maxlength="1000"

// 3. Image limit
:max-images="5"
```

---

## 🐛 Troubleshooting

### Lỗi 1: Backend không khởi động
```
Error: Table 'REVIEWS' doesn't exist
Solution: Chạy lại SQL migration script
```

### Lỗi 2: Nút đánh giá không hiện
```
Check:
1. Order status phải là PAID/SHIPPING/DELIVERED
2. Chưa đánh giá sản phẩm này
3. Console log có error không
```

### Lỗi 3: Submit review bị lỗi 401
```
Check:
1. JWT token còn hạn không (localStorage)
2. Header Authorization có đúng format không
3. Backend SecurityConfig cho phép /api/reviews POST
```

### Lỗi 4: Upload ảnh không thành công
```
Check:
1. Cloudinary credentials trong backend
2. MultiImageUpload component đang dùng đúng API
3. Network tab có lỗi upload không
```

---

## 📈 Performance Considerations

### Database Indexes
```sql
-- Đã tạo sẵn indexes:
- IDX_REVIEWS_PRODUCT (productId)
- IDX_REVIEWS_USER (userId)  
- IDX_REVIEWS_ORDER (orderId)
- IDX_REVIEWS_RATING (rating)

-- Giúp query nhanh khi:
- Lấy reviews của sản phẩm
- Filter theo rating
- Lấy reviews của user
```

### API Optimization
```java
// Eager fetch images trong ReviewRepository nếu cần
@EntityGraph(attributePaths = {"images"})
List<Review> findByProductIdOrderByCreatedAtDesc(Integer productId);
```

---

## 🎨 UI/UX Features

### ReviewForm.vue
- ⭐ Interactive star rating với hover effect
- 📝 Character counter (0/1000)
- 📸 Multi-image upload với preview
- ✅ Real-time validation
- 🎨 Smooth animations

### ReviewList.vue
- 📊 Rating summary với average score
- 📈 Visual rating distribution bars
- 💬 User reviews với avatar
- 📸 Image gallery với modal
- 📅 Relative time display ("2 ngày trước")

---

## 🚀 Next Steps (Tính Năng Mở Rộng)

### 1. Review Moderation (Duyệt đánh giá)
```
- Admin có thể approve/reject reviews
- Thêm field STATUS trong REVIEWS table
- Filter chỉ show approved reviews
```

### 2. Review Response (Phản hồi đánh giá)
```
- Shop owner có thể reply review
- Tạo bảng REVIEW_RESPONSES
- Hiển thị reply bên dưới review
```

### 3. Helpful Voting (Bình chọn hữu ích)
```
- User vote review helpful/not helpful
- Tạo bảng REVIEW_VOTES
- Sắp xếp reviews theo số vote
```

### 4. Verified Purchase Badge
```
- Thêm badge "✓ Đã mua hàng"
- Tạo thêm trong ReviewList.vue
```

---

## ✅ Checklist Hoàn Thành

- [x] Tạo database tables (REVIEWS, REVIEW_IMAGES)
- [x] Tạo entities (Review, ReviewImage)
- [x] Tạo repository với query methods
- [x] Tạo service với business logic
- [x] Tạo controller với REST endpoints
- [x] Tạo DTO (ReviewRequest)
- [x] Tạo ReviewForm component
- [x] Tạo ReviewList component
- [x] Update Orders.vue với nút đánh giá
- [x] Update ProductDetail.vue với hiển thị reviews
- [x] Update api.js với review methods
- [x] Test validation rules
- [x] Test UI/UX workflow

---

## 📞 Support

Nếu gặp vấn đề:
1. Check console logs (browser + backend)
2. Kiểm tra database có data không
3. Test API với Postman
4. Review code trong file này

**Chúc bạn triển khai thành công! 🎉**
