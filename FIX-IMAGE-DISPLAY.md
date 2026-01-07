# 🔧 Fix: Hiển thị ảnh sản phẩm ở trang chủ

## 🐛 VẤN ĐỀ
Sau khi triển khai tính năng nhiều ảnh (images array), trang chủ không hiển thị ảnh sản phẩm vì:
- Backend giờ trả về `images[]` array thay vì `imageUrl` string
- Frontend components vẫn đang tìm `product.imageUrl`

## ✅ ĐÃ SỬA

### 1. ProductCard.vue (Component hiển thị sản phẩm ở trang chủ)
**Vấn đề:** Đang dùng `product.imageUrl` trực tiếp
**Giải pháp:** Thêm computed property `productImage` để xử lý cả 2 format

```vue
// Template
<img :src="productImage" ... />

// Script
const productImage = computed(() => {
  // Ưu tiên lấy từ images array (new format)
  if (props.product.images && props.product.images.length > 0) {
    return props.product.images[0].imageUrl
  }
  // Fallback sang imageUrl cũ (backward compatibility)
  return props.product.imageUrl || null
})
```

### 2. Cart.vue (Giỏ hàng)
**Vấn đề:** Đang dùng `item.product?.imageUrl`
**Giải pháp:** Thêm helper function `getProductImage()`

```javascript
const getProductImage = (product) => {
  if (!product) return null
  // Ưu tiên lấy từ images array
  if (product.images && product.images.length > 0) {
    return product.images[0].imageUrl
  }
  // Fallback sang imageUrl cũ
  return product.imageUrl || null
}
```

### 3. ProductManagement.vue (Admin - Quản lý sản phẩm)
**Đã được cập nhật trước đó:**
- Sử dụng `getProductImage()` helper function
- Hiển thị ảnh đầu tiên từ array

### 4. ProductDetail.vue (Trang chi tiết sản phẩm)
**Đã được cập nhật trước đó:**
- Computed property `productImages` để lấy array ảnh
- Thumbnail gallery để chuyển đổi giữa các ảnh

## 🎯 KẾT QUẢ
✅ Trang chủ hiển thị ảnh bình thường  
✅ Giỏ hàng hiển thị ảnh sản phẩm  
✅ Trang chi tiết hiển thị gallery nhiều ảnh  
✅ Admin quản lý upload/hiển thị nhiều ảnh  
✅ Backward compatible với dữ liệu cũ (imageUrl)

## 📝 LOGIC XỬ LÝ ẢNH

### Thứ tự ưu tiên:
1. **Kiểm tra `product.images` array** (format mới)
   - Nếu có → Lấy ảnh đầu tiên: `product.images[0].imageUrl`
2. **Fallback sang `product.imageUrl`** (format cũ)
   - Nếu có → Lấy trực tiếp
3. **Không có ảnh**
   - Hiển thị placeholder (icon 📱)

### Code pattern tái sử dụng:
```javascript
// Computed property (Vue 3 Composition API)
const productImage = computed(() => {
  if (props.product.images?.length > 0) {
    return props.product.images[0].imageUrl
  }
  return props.product.imageUrl || null
})

// Helper function (Vue 3 Options API)
const getProductImage = (product) => {
  if (product?.images?.length > 0) {
    return product.images[0].imageUrl
  }
  return product?.imageUrl || null
}
```

## 🚀 CÁCH TEST
1. Restart backend & frontend
2. Vào trang chủ: http://localhost:5173
3. Kiểm tra ảnh sản phẩm hiển thị đúng
4. Thêm sản phẩm vào giỏ → Check ảnh trong giỏ
5. Vào chi tiết sản phẩm → Check gallery nhiều ảnh
6. Admin thêm/sửa sản phẩm với nhiều ảnh → Check hiển thị

## 📌 LƯU Ý
- Code đã được thiết kế **backward compatible**
- Sản phẩm cũ (chỉ có imageUrl) vẫn hoạt động bình thường
- Sản phẩm mới (có images array) sẽ hiển thị ảnh từ array
- Ảnh đầu tiên trong array = ảnh chính (isPrimary = true)
