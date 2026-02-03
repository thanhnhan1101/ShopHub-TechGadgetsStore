-- =====================================================
-- Sample Review Data
-- Insert sample reviews for testing
-- =====================================================

-- Kiểm tra xem có dữ liệu review nào chưa
SELECT COUNT(*) AS 'Current Review Count' FROM REVIEWS;

-- Insert sample reviews cho các sản phẩm
-- Giả sử: User ID 2 (customer), Order ID 1, các Product IDs từ 1-5

-- Review 1: iPhone 15 Pro Max (5 sao)
INSERT INTO REVIEWS (PRODUCT_ID, USER_ID, ORDER_ID, RATING, COMMENT, CREATED_AT)
VALUES (
    1, 
    2, 
    1, 
    5, 
    N'Điện thoại rất tuyệt vời! Màn hình đẹp, camera chụp ảnh sắc nét. Giao hàng nhanh, đóng gói cẩn thận. Rất hài lòng với sản phẩm!',
    DATEADD(day, -5, GETDATE())
);

-- Lấy ID của review vừa tạo và thêm ảnh
DECLARE @ReviewId1 INT = SCOPE_IDENTITY();
INSERT INTO REVIEW_IMAGES (REVIEW_ID, IMAGE_URL, DISPLAY_ORDER)
VALUES 
    (@ReviewId1, 'https://res.cloudinary.com/demo/image/upload/sample.jpg', 1);

-- Review 2: Samsung Galaxy S24 Ultra (4 sao)
INSERT INTO REVIEWS (PRODUCT_ID, USER_ID, ORDER_ID, RATING, COMMENT, CREATED_AT)
VALUES (
    2, 
    2, 
    1, 
    4, 
    N'Sản phẩm chất lượng tốt, pin trâu. Tuy nhiên giá hơi cao so với mong đợi. Nhìn chung vẫn đáng mua!',
    DATEADD(day, -4, GETDATE())
);

-- Review 3: MacBook Pro M3 (5 sao)
INSERT INTO REVIEWS (PRODUCT_ID, USER_ID, ORDER_ID, RATING, COMMENT, CREATED_AT)
VALUES (
    3, 
    2, 
    1, 
    5, 
    N'Laptop tuyệt vời cho công việc! Hiệu năng mạnh mẽ, thiết kế đẹp, pin lâu. Rất đáng đồng tiền bát gạo!',
    DATEADD(day, -3, GETDATE())
);

-- Review 4: Dell XPS 15 (4 sao) 
INSERT INTO REVIEWS (PRODUCT_ID, USER_ID, ORDER_ID, RATING, COMMENT, CREATED_AT)
VALUES (
    4, 
    2, 
    1, 
    4, 
    N'Máy chạy nhanh, màn hình đẹp. Chỉ có điều hơi nóng khi dùng lâu. Tổng thể ok!',
    DATEADD(day, -2, GETDATE())
);

-- Review 5: iPad Pro 12.9 (5 sao)
INSERT INTO REVIEWS (PRODUCT_ID, USER_ID, ORDER_ID, RATING, COMMENT, CREATED_AT)
VALUES (
    5, 
    2, 
    1, 
    5, 
    N'iPad tuyệt vời cho việc vẽ và làm việc! Màn hình lớn, mượt mà. Rất hài lòng!',
    DATEADD(day, -1, GETDATE())
);

-- Thêm vài review nữa với rating khác nhau để test rating distribution

-- Review 6: iPhone 15 Pro Max (4 sao) - user khác
INSERT INTO REVIEWS (PRODUCT_ID, USER_ID, ORDER_ID, RATING, COMMENT, CREATED_AT)
VALUES (
    1, 
    3, 
    2, 
    4, 
    N'Điện thoại tốt nhưng giá hơi cao. Camera thì không có gì để chê!',
    DATEADD(hour, -12, GETDATE())
);

-- Review 7: iPhone 15 Pro Max (3 sao) - user khác
INSERT INTO REVIEWS (PRODUCT_ID, USER_ID, ORDER_ID, RATING, COMMENT, CREATED_AT)
VALUES (
    1, 
    4, 
    3, 
    3, 
    N'Sản phẩm ổn, nhưng không có gì đặc biệt lắm.',
    DATEADD(hour, -6, GETDATE())
);

-- Review 8: Samsung Galaxy S24 Ultra (5 sao)
INSERT INTO REVIEWS (PRODUCT_ID, USER_ID, ORDER_ID, RATING, COMMENT, CREATED_AT)
VALUES (
    2, 
    3, 
    2, 
    5, 
    N'Điện thoại tuyệt vời! S Pen rất hữu ích. Recommend 10/10!',
    DATEADD(hour, -3, GETDATE())
);

-- Hiển thị kết quả
SELECT 
    p.NAME as 'Product Name',
    COUNT(r.ID) as 'Review Count',
    AVG(CAST(r.RATING as FLOAT)) as 'Average Rating'
FROM PRODUCTS p
LEFT JOIN REVIEWS r ON p.ID = r.PRODUCT_ID
GROUP BY p.NAME
ORDER BY 'Review Count' DESC;

SELECT '✅ Sample reviews inserted successfully!' as Status;
