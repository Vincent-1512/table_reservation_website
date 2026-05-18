SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE order_items;
TRUNCATE TABLE payments;
TRUNCATE TABLE orders;
TRUNCATE TABLE reservations;
TRUNCATE TABLE menu_items;
TRUNCATE TABLE categories;
TRUNCATE TABLE tables;
TRUNCATE TABLE areas;

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
INSERT INTO tables (id, table_name, capacity, status, area_id, version, updated_at) VALUES
(1, 'Bàn 01', 2, 'AVAILABLE', 1, 0, NOW()),
(2, 'Bàn 02', 4, 'AVAILABLE', 1, 0, NOW()),
(3, 'Bàn 03', 4, 'AVAILABLE', 1, 0, NOW()),
(4, 'Bàn 04', 6, 'AVAILABLE', 1, 0, NOW()),
(5, 'Bàn 05', 2, 'AVAILABLE', 2, 0, NOW()),
(6, 'Bàn 06', 4, 'AVAILABLE', 2, 0, NOW()),
(7, 'Bàn 07', 6, 'AVAILABLE', 2, 0, NOW()),
(8, 'Bàn 08', 8, 'AVAILABLE', 2, 0, NOW()),
(9, 'Bàn 09', 4, 'AVAILABLE', 3, 0, NOW()),
(10, 'Bàn 10', 6, 'AVAILABLE', 3, 0, NOW());

-- Thêm Menu Items
INSERT INTO menu_items (name, description, price, is_available, category_id, created_at, updated_at) VALUES
('Gỏi cuốn tôm thịt', 'Gỏi cuốn tươi với tôm, thịt heo, rau sống', 45000, 1, 1, NOW(), NOW()),
('Chả giò chiên', 'Chả giò nhân thịt và rau củ, giòn rụm', 55000, 1, 1, NOW(), NOW()),
('Salad Caesar', 'Xà lách Caesar với sốt kem, crouton và phô mai', 75000, 1, 1, NOW(), NOW()),
('Cơm chiên dương châu', 'Cơm chiên trứng, tôm, thịt xá xíu', 85000, 1, 2, NOW(), NOW()),
('Bò lúc lắc', 'Thịt bò xào bơ tỏi, ăn kèm cơm trắng', 145000, 1, 2, NOW(), NOW()),
('Mì xào hải sản', 'Mì trứng xào với hải sản tươi và rau củ', 115000, 1, 2, NOW(), NOW()),
('Cơm tấm sườn bì chả', 'Cơm tấm truyền thống với sườn nướng, bì, chả', 95000, 1, 2, NOW(), NOW()),
('Lẩu thái hải sản', 'Lẩu thái chua cay với hải sản tươi', 285000, 1, 2, NOW(), NOW()),
('Tôm hùm hấp', 'Tôm hùm hấp gừng lá sả', 650000, 1, 3, NOW(), NOW()),
('Cua rang me', 'Cua sốt me chua ngọt đặc trưng', 320000, 1, 3, NOW(), NOW()),
('Mực nướng sa tế', 'Mực ống nướng sa tế thơm cay', 165000, 1, 3, NOW(), NOW()),
('Cá hồi áp chảo', 'Philê cá hồi áp chảo sốt chanh bơ', 195000, 1, 3, NOW(), NOW()),
('Sinh tố bơ', 'Sinh tố bơ béo ngậy, kem tươi', 55000, 1, 4, NOW(), NOW()),
('Nước cam tươi', 'Cam tươi nguyên chất', 45000, 1, 4, NOW(), NOW()),
('Trà đào cam sả', 'Trà đào mát lạnh với cam và lá sả', 55000, 1, 4, NOW(), NOW()),
('Cà phê sữa đá', 'Cà phê phin truyền thống pha sữa đặc', 35000, 1, 4, NOW(), NOW()),
('Bia Tiger (lon)', 'Bia Tiger 330ml lạnh', 30000, 1, 4, NOW(), NOW()),
('Chè ba màu', 'Chè ba màu truyền thống với đậu, thạch và nước cốt dừa', 35000, 1, 5, NOW(), NOW()),
('Kem dừa', 'Kem dừa tươi phủ cùi dừa nạo', 45000, 1, 5, NOW(), NOW()),
('Bánh tiramisu', 'Bánh tiramisu Ý với cà phê espresso', 75000, 1, 5, NOW(), NOW());
