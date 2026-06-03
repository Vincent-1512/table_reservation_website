SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE order_items;
TRUNCATE TABLE payments;
TRUNCATE TABLE orders;
TRUNCATE TABLE reservations;
TRUNCATE TABLE menu_items;
TRUNCATE TABLE categories;
TRUNCATE TABLE `tables`;
TRUNCATE TABLE areas;
TRUNCATE TABLE users;

SET FOREIGN_KEY_CHECKS = 1;

-- Thêm Categories
INSERT INTO categories (id, name, description) VALUES
(1, 'Khai vị', 'Các món ăn khai vị, gỏi, salad'),
(2, 'Món chính', 'Các món chính như cơm, mì, lẩu'),
(3, 'Hải sản', 'Các món hải sản tươi sống'),
(4, 'Thức uống', 'Đồ uống, sinh tố, nước ép'),
(5, 'Tráng miệng', 'Các loại bánh, kem, chè');

-- Thêm Areas
INSERT INTO areas (id, name, description, created_at, updated_at) VALUES
(1, 'Tầng 1', 'Khu vực tầng trệt, thoáng mát', NOW(), NOW()),
(2, 'Tầng 2', 'Khu vực tầng 2, view đẹp', NOW(), NOW()),
(3, 'Sân thượng', 'Khu vực ngoài trời, lãng mạn', NOW(), NOW());

-- Thêm Tables
INSERT INTO `tables` (id, table_name, capacity, status, area_id, updated_at)
VALUES
(1, 'Bàn 01', 2, 'AVAILABLE', 1, NOW()),
(2, 'Bàn 02', 4, 'AVAILABLE', 1, NOW()),
(3, 'Bàn 03', 4, 'AVAILABLE', 1, NOW()),
(4, 'Bàn 04', 6, 'AVAILABLE', 1, NOW()),
(5, 'Bàn 05', 2, 'AVAILABLE', 2, NOW()),
(6, 'Bàn 06', 4, 'AVAILABLE', 2, NOW()),
(7, 'Bàn 07', 6, 'AVAILABLE', 2, NOW()),
(8, 'Bàn 08', 8, 'AVAILABLE', 2, NOW()),
(9, 'Bàn 09', 4, 'AVAILABLE', 3, NOW()),
(10, 'Bàn 10', 6, 'AVAILABLE', 3, NOW());

-- Thêm Menu Items
INSERT INTO menu_items (name, description, price, is_available, category_id, image_url, created_at, updated_at) VALUES
('Gỏi cuốn tôm thịt', 'Gỏi cuốn tươi với tôm, thịt heo, rau sống', 45000, 1, 1, 'https://images.unsplash.com/photo-1559314809-0d155014e29e?w=800&q=80', NOW(), NOW()),
('Chả giò chiên', 'Chả giò nhân thịt và rau củ, giòn rụm', 55000, 1, 1, 'https://images.unsplash.com/photo-1606756616016-1f6b5b5fc036?w=800&q=80', NOW(), NOW()),
('Salad Caesar', 'Xà lách Caesar với sốt kem, crouton và phô mai', 75000, 1, 1, 'https://images.unsplash.com/photo-1550304943-4f24f54ddde9?w=800&q=80', NOW(), NOW()),
('Cơm chiên dương châu', 'Cơm chiên trứng, tôm, thịt xá xíu', 85000, 1, 2, 'https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=800&q=80', NOW(), NOW()),
('Bò lúc lắc', 'Thịt bò xào bơ tỏi, ăn kèm cơm trắng', 145000, 1, 2, 'https://images.unsplash.com/photo-1544025162-8314441548fb?w=800&q=80', NOW(), NOW()),
('Mì xào hải sản', 'Mì trứng xào với hải sản tươi và rau củ', 115000, 1, 2, 'https://images.unsplash.com/photo-1585032226651-759b368d7246?w=800&q=80', NOW(), NOW()),
('Cơm tấm sườn bì chả', 'Cơm tấm truyền thống với sườn nướng, bì, chả', 95000, 1, 2, 'https://images.unsplash.com/photo-1615486171434-2e9ebdb1c620?w=800&q=80', NOW(), NOW()),
('Lẩu thái hải sản', 'Lẩu thái chua cay với hải sản tươi', 285000, 1, 2, 'https://images.unsplash.com/photo-1548943487-a2e4b43b3749?w=800&q=80', NOW(), NOW()),
('Tôm hùm hấp', 'Tôm hùm hấp gừng lá sả', 650000, 1, 3, 'https://images.unsplash.com/photo-1559742811-822873691df8?w=800&q=80', NOW(), NOW()),
('Cua rang me', 'Cua sốt me chua ngọt đặc trưng', 320000, 1, 3, 'https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=800&q=80', NOW(), NOW()),
('Mực nướng sa tế', 'Mực ống nướng sa tế thơm cay', 165000, 1, 3, 'https://images.unsplash.com/photo-1599084993091-1cb5c0721cc6?w=800&q=80', NOW(), NOW()),
('Cá hồi áp chảo', 'Philê cá hồi áp chảo sốt chanh bơ', 195000, 1, 3, 'https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=800&q=80', NOW(), NOW()),
('Sinh tố bơ', 'Sinh tố bơ béo ngậy, kem tươi', 55000, 1, 4, 'https://images.unsplash.com/photo-1603569283847-aa295f0d016a?w=800&q=80', NOW(), NOW()),
('Nước cam tươi', 'Cam tươi nguyên chất', 45000, 1, 4, 'https://images.unsplash.com/photo-1611162616475-46b635cb6868?w=800&q=80', NOW(), NOW()),
('Trà đào cam sả', 'Trà đào mát lạnh với cam và lá sả', 55000, 1, 4, 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=800&q=80', NOW(), NOW()),
('Cà phê sữa đá', 'Cà phê phin truyền thống pha sữa đặc', 35000, 1, 4, 'https://images.unsplash.com/photo-1578314675249-a6910f80cc4e?w=800&q=80', NOW(), NOW()),
('Bia Tiger (lon)', 'Bia Tiger 330ml lạnh', 30000, 1, 4, 'https://images.unsplash.com/photo-1608270586620-248524c67de9?w=800&q=80', NOW(), NOW()),
('Chè ba màu', 'Chè ba màu truyền thống với đậu, thạch và nước cốt dừa', 35000, 1, 5, 'https://images.unsplash.com/photo-1563805042-7684c8a9e9cb?w=800&q=80', NOW(), NOW()),
('Kem dừa', 'Kem dừa tươi phủ cùi dừa nạo', 45000, 1, 5, 'https://images.unsplash.com/photo-1553177595-4de2bb0842b9?w=800&q=80', NOW(), NOW()),
('Bánh tiramisu', 'Bánh tiramisu Ý với cà phê espresso', 75000, 1, 5, 'https://images.unsplash.com/photo-1571115177098-24ec42ed204d?w=800&q=80', NOW(), NOW());

-- Thêm Users (password: 123456)
INSERT INTO users (username, password_hash, full_name, phone, email, role, created_at, updated_at, deleted) VALUES
('admin', '$2a$10$0w7tJykpZacr5NEbM.wvIeHTKViJ7ojyaB5iOEKKx/oFzkV40koMm', 'Quản trị viên', '0987654321', 'admin@restaurant.com', 'ADMIN', NOW(), NOW(), 0),
('staff', '$2a$10$0w7tJykpZacr5NEbM.wvIeHTKViJ7ojyaB5iOEKKx/oFzkV40koMm', 'Nhân viên 1', '0123456789', 'staff@restaurant.com', 'STAFF', NOW(), NOW(), 0),
('customer', '$2a$10$0w7tJykpZacr5NEbM.wvIeHTKViJ7ojyaB5iOEKKx/oFzkV40koMm', 'Khách hàng 1', '0333333333', 'customer@restaurant.com', 'CUSTOMER', NOW(), NOW(), 0);
