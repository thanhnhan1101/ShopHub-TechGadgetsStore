-- =====================================================
-- Migration Script: Thêm hỗ trợ nhiều ảnh cho sản phẩm
-- Ngày tạo: 2026-01-07
-- =====================================================

-- 1. Tạo bảng PRODUCT_IMAGES
CREATE TABLE PRODUCT_IMAGES (
    ID INT IDENTITY(1,1) PRIMARY KEY,
    PRODUCT_ID INT NOT NULL,
    IMAGE_URL NVARCHAR(MAX) NOT NULL,
    DISPLAY_ORDER INT NOT NULL DEFAULT 0,
    IS_PRIMARY BIT NOT NULL DEFAULT 0,
    
    -- Foreign key constraint
    CONSTRAINT FK_PRODUCT_IMAGES_PRODUCT 
        FOREIGN KEY (PRODUCT_ID) 
        REFERENCES PRODUCTS(ID) 
        ON DELETE CASCADE
);

-- 2. Tạo index để tăng performance khi query
CREATE INDEX IDX_PRODUCT_IMAGES_PRODUCT ON PRODUCT_IMAGES(PRODUCT_ID);
CREATE INDEX IDX_PRODUCT_IMAGES_PRIMARY ON PRODUCT_IMAGES(PRODUCT_ID, IS_PRIMARY);

-- 3. Migrate dữ liệu cũ từ PRODUCTS.IMAGE_URL sang PRODUCT_IMAGES
-- Chỉ migrate những sản phẩm có ảnh
INSERT INTO PRODUCT_IMAGES (PRODUCT_ID, IMAGE_URL, DISPLAY_ORDER, IS_PRIMARY)
SELECT 
    ID as PRODUCT_ID,
    IMAGE_URL,
    0 as DISPLAY_ORDER,
    1 as IS_PRIMARY
FROM PRODUCTS
WHERE IMAGE_URL IS NOT NULL 
  AND IMAGE_URL != '';

-- 4. (TÙY CHỌN) Xóa cột IMAGE_URL cũ sau khi đã migrate xong và kiểm tra kỹ
-- Cẩn thận: Chỉ chạy lệnh này khi đã chắc chắn dữ liệu đã được migrate đúng
-- ALTER TABLE PRODUCTS DROP COLUMN IMAGE_URL;

-- 5. Kiểm tra kết quả migration
SELECT 
    COUNT(*) as TotalProductsWithOldImages
FROM PRODUCTS 
WHERE IMAGE_URL IS NOT NULL AND IMAGE_URL != '';

SELECT 
    COUNT(*) as TotalNewImages
FROM PRODUCT_IMAGES;

-- Kiểm tra có sản phẩm nào có nhiều hơn 1 ảnh không (sau khi user bắt đầu sử dụng)
SELECT 
    PRODUCT_ID, 
    COUNT(*) as ImageCount
FROM PRODUCT_IMAGES
GROUP BY PRODUCT_ID
HAVING COUNT(*) > 1;

-- Hiển thị một vài ví dụ
SELECT TOP 5
    p.ID as ProductId,
    p.NAME as ProductName,
    pi.IMAGE_URL,
    pi.DISPLAY_ORDER,
    pi.IS_PRIMARY
FROM PRODUCTS p
INNER JOIN PRODUCT_IMAGES pi ON p.ID = pi.PRODUCT_ID
ORDER BY p.ID, pi.DISPLAY_ORDER;
