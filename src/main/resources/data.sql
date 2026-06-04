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

-- Thêm Menu Items (bao gồm story và ingredients)
INSERT INTO menu_items (name, description, story, ingredients, price, is_available, category_id, image_url, created_at, updated_at) VALUES
('Gỏi cuốn tôm thịt', 'Gỏi cuốn tươi với tôm, thịt heo, rau sống',
 'Gỏi cuốn là món ăn dân dã của miền Nam Việt Nam, ra đời từ những bữa cơm gia đình giản dị. Sự kết hợp giữa tôm tươi, thịt luộc và rau sống cuốn trong lớp bánh tráng mỏng tạo nên hương vị thanh mát đặc trưng. Tại Lumina, chúng tôi chọn tôm sú tươi sống từ biển Phan Thiết, đảm bảo mỗi cuốn gỏi đều mang đậm vị ngọt tự nhiên.',
 'Tôm sú tươi, thịt ba chỉ luộc, bún tươi, rau xà lách, húng quế, giá đỗ, bánh tráng, nước mắm pha',
 45000, 1, 1, 'https://images.unsplash.com/photo-1559314809-0d155014e29e?w=800&q=80', NOW(), NOW()),

('Chả giò chiên', 'Chả giò nhân thịt và rau củ, giòn rụm',
 'Chả giò - hay còn gọi là nem rán ở miền Bắc - là niềm tự hào ẩm thực Việt Nam trên bàn tiệc quốc tế. Mỗi chiếc chả giò tại Lumina được cuốn tay thủ công, chiên giòn vàng ươm. Bí quyết nằm ở lớp vỏ bánh tráng được chiên hai lần: lần đầu ở nhiệt độ thấp để chín đều, lần sau ở nhiệt độ cao để giòn rụm.',
 'Thịt heo xay, miến dong, mộc nhĩ, cà rốt bào, hành tây, trứng gà, tiêu đen, bánh tráng nem',
 55000, 1, 1, 'https://images.unsplash.com/photo-1606756616016-1f6b5b5fc036?w=800&q=80', NOW(), NOW()),

('Salad Caesar', 'Xà lách Caesar với sốt kem, crouton và phô mai',
 'Salad Caesar được sáng tạo bởi đầu bếp Caesar Cardini tại Mexico năm 1924 trong một đêm nhà hàng hết nguyên liệu. Tại Lumina, chúng tôi tái hiện công thức kinh điển này với xà lách romaine giòn tươi từ Đà Lạt, sốt Caesar tự làm từ trứng, dầu ô liu và cá cơm, kết hợp cùng crouton nướng giòn và phô mai Parmesan nhập khẩu từ Ý.',
 'Xà lách romaine Đà Lạt, crouton nướng bơ tỏi, phô mai Parmesan, sốt Caesar (lòng đỏ trứng, dầu ô liu, tỏi, chanh, cá cơm, mù tạt Dijon)',
 75000, 1, 1, 'https://images.unsplash.com/photo-1550304943-4f24f54ddde9?w=800&q=80', NOW(), NOW()),

('Cơm chiên dương châu', 'Cơm chiên trứng, tôm, thịt xá xíu',
 'Cơm chiên Dương Châu có nguồn gốc từ thành phố Dương Châu, tỉnh Giang Tô, Trung Quốc - nơi nổi tiếng với nghệ thuật ẩm thực tinh tế. Bí quyết của món cơm chiên hoàn hảo nằm ở việc sử dụng cơm nguội qua đêm, chiên ở lửa lớn với chảo gang nóng bỏng. Tại Lumina, chúng tôi dùng gạo ST25 - loại gạo ngon nhất thế giới - để tạo nên từng hạt cơm tơi xốp, thơm lừng.',
 'Cơm gạo ST25, tôm tươi, xá xíu thái hạt lựu, trứng gà, đậu Hà Lan, cà rốt, hành lá, dầu mè',
 85000, 1, 2, 'https://images.unsplash.com/photo-1603133872878-684f208fb84b?w=800&q=80', NOW(), NOW()),

('Bò lúc lắc', 'Thịt bò xào bơ tỏi, ăn kèm cơm trắng',
 'Bò lúc lắc là sự giao thoa tuyệt vời giữa ẩm thực Pháp và Việt Nam, ra đời trong thời kỳ Pháp thuộc tại Sài Gòn. Cái tên "lúc lắc" bắt nguồn từ âm thanh thịt bò nhảy trong chảo nóng. Tại Lumina, chúng tôi chọn thịt bò Úc thượng hạng, thái hạt lựu vuông vắn, ướp với nước tương và tỏi qua đêm, rồi xào trên lửa lớn với bơ Pháp để giữ trọn vị mềm ngọt.',
 'Thịt bò Úc thăn nội, bơ Pháp, tỏi băm, hành tây, ớt chuông, nước tương Maggi, dầu hào, tiêu đen xay',
 145000, 1, 2, 'https://images.unsplash.com/photo-1544025162-8314441548fb?w=800&q=80', NOW(), NOW()),

('Mì xào hải sản', 'Mì trứng xào với hải sản tươi và rau củ',
 'Mì xào hải sản là món ăn quen thuộc tại các nhà hàng ven biển miền Trung Việt Nam. Sợi mì trứng dai mềm quyện cùng hải sản tươi rói tạo nên bản hòa tấu của biển cả. Tại Lumina, hải sản được nhập tươi sống mỗi sáng từ cảng cá Vũng Tàu, đảm bảo vị ngọt tự nhiên nhất trong từng sợi mì.',
 'Mì trứng, tôm sú, mực ống, nghêu, bông cải xanh, cà rốt, nấm đông cô, hành tỏi, dầu hào, nước tương',
 115000, 1, 2, 'https://images.unsplash.com/photo-1585032226651-759b368d7246?w=800&q=80', NOW(), NOW()),

('Cơm tấm sườn bì chả', 'Cơm tấm truyền thống với sườn nướng, bì, chả',
 'Cơm tấm Sài Gòn - món ăn bình dị nhưng đầy tự hào của người miền Nam. Từ hạt gạo tấm vỡ bị bỏ đi, người Sài Gòn đã biến nó thành biểu tượng ẩm thực đường phố. Tại Lumina, sườn được ướp hơn 12 tiếng với sả, tỏi và nước mắm rồi nướng trên than hoa, tạo lớp caramel vàng óng ánh. Bì và chả được làm tay mỗi ngày theo công thức gia truyền.',
 'Cơm tấm, sườn heo nướng than, bì heo, chả trứng hấp, mỡ hành, đồ chua (cà rốt, củ cải), nước mắm pha',
 95000, 1, 2, 'https://images.unsplash.com/photo-1615486171434-2e9ebdb1c620?w=800&q=80', NOW(), NOW()),

('Lẩu thái hải sản', 'Lẩu thái chua cay với hải sản tươi',
 'Lẩu Thái Tom Yum có nguồn gốc từ miền Trung Thái Lan, nơi dòng sông Chao Phraya mang đến nguồn hải sản dồi dào. Vị chua của chanh, cay của ớt, thơm của sả và ngọt của hải sản hòa quyện tạo nên hương vị không thể nhầm lẫn. Tại Lumina, nước dùng lẩu được ninh từ xương tôm hùm và sả tươi trong 6 tiếng, mang đến độ đậm đà tự nhiên mà không cần dùng bột nêm.',
 'Tôm sú, mực, nghêu, cá basa, nấm kim châm, nấm đùi gà, cà chua, sả, lá chanh, ớt hiểm, galangal, nước cốt dừa, me',
 285000, 1, 2, 'https://images.unsplash.com/photo-1548943487-a2e4b43b3749?w=800&q=80', NOW(), NOW()),

('Tôm hùm hấp', 'Tôm hùm hấp gừng lá sả',
 'Tôm hùm - vua của các loài hải sản - từng là thức ăn của tầng lớp lao động ven biển New England trước khi trở thành món ăn xa xỉ bậc nhất thế giới. Tại Lumina, tôm hùm xanh được đặt riêng từ vùng biển Bình Ba (Khánh Hòa), nơi có dòng nước trong xanh và san hô phong phú. Mỗi con tôm hùm nặng từ 500g trở lên, hấp cùng gừng tươi và sả để giữ trọn vị ngọt thanh tự nhiên.',
 'Tôm hùm xanh Bình Ba (500g+), gừng tươi thái sợi, sả cây, hành lá, nước mắm Phú Quốc, chanh tươi',
 650000, 1, 3, 'https://images.unsplash.com/photo-1559742811-822873691df8?w=800&q=80', NOW(), NOW()),

('Cua rang me', 'Cua sốt me chua ngọt đặc trưng',
 'Cua rang me là đặc sản nổi tiếng của ẩm thực Sài Gòn, xuất hiện lần đầu tại các nhà hàng hải sản đường Nguyễn Trãi, Quận 5 vào những năm 1980. Sự kết hợp giữa vị chua tự nhiên của me và vị ngọt của cua biển tạo nên món ăn gây nghiện. Tại Lumina, cua gạch được chọn lọc kỹ từng con, rang với sốt me được nấu từ me chín Tây Ninh, tỏi phi và đường thốt nốt.',
 'Cua biển gạch son, me chín Tây Ninh, tỏi phi, hành phi, đường thốt nốt, nước mắm, ớt sừng, hành lá',
 320000, 1, 3, 'https://images.unsplash.com/photo-1565557623262-b51c2513a641?w=800&q=80', NOW(), NOW()),

('Mực nướng sa tế', 'Mực ống nướng sa tế thơm cay',
 'Mực nướng sa tế gợi nhớ những buổi chiều biển vàng ở Mũi Né, nơi ngư dân nướng mực tươi ngay trên bờ cát. Sa tế - hỗn hợp gia vị có nguồn gốc từ Indonesia - khi kết hợp với mực tươi tạo nên vị cay thơm nồng nàn khó quên. Tại Lumina, mực ống được chọn loại size lớn từ biển Phan Thiết, nướng trên than hoa với lớp sa tế tự pha chế từ ớt khô, sả và tôm khô xay nhuyễn.',
 'Mực ống tươi Phan Thiết, sa tế tự pha (ớt khô, sả, tôm khô, tỏi, dầu điều), hành tím, chanh, rau răm',
 165000, 1, 3, 'https://images.unsplash.com/photo-1599084993091-1cb5c0721cc6?w=800&q=80', NOW(), NOW()),

('Cá hồi áp chảo', 'Philê cá hồi áp chảo sốt chanh bơ',
 'Cá hồi áp chảo sốt chanh bơ là sự kết hợp hoàn hảo giữa kỹ thuật nấu ăn Pháp và nguyên liệu tươi ngon. Bí quyết để có miếng cá hồi hoàn hảo là áp chảo ở nhiệt độ cao, tạo lớp vỏ vàng giòn bên ngoài trong khi bên trong vẫn mềm ẩm và hồng đào. Tại Lumina, chúng tôi sử dụng cá hồi Na Uy nhập khẩu nguyên con, philê tại chỗ để đảm bảo độ tươi tuyệt đối.',
 'Cá hồi Na Uy phi lê, bơ Pháp Président, chanh vàng, tỏi, cây thì là, muối biển, tiêu đen Phú Quốc, khoai tây baby',
 195000, 1, 3, 'https://images.unsplash.com/photo-1467003909585-2f8a72700288?w=800&q=80', NOW(), NOW()),

('Sinh tố bơ', 'Sinh tố bơ béo ngậy, kem tươi',
 'Sinh tố bơ là thức uống đặc trưng của Việt Nam, đặc biệt phổ biến tại các quán cà phê vỉa hè Sài Gòn từ những năm 1990. Quả bơ sáp Đắk Lắk với thịt vàng ươm, béo ngậy tự nhiên, khi xay cùng sữa đặc và đá tạo nên ly sinh tố mịn màng như nhung. Tại Lumina, chúng tôi chỉ sử dụng bơ sáp 034 Đắk Lắk - giống bơ ngon nhất Việt Nam - thu hoạch đúng mùa.',
 'Bơ sáp 034 Đắk Lắk, sữa đặc Ông Thọ, kem tươi whipping, đá xay, lá bạc hà trang trí',
 55000, 1, 4, 'https://images.unsplash.com/photo-1603569283847-aa295f0d016a?w=800&q=80', NOW(), NOW()),

('Nước cam tươi', 'Cam tươi nguyên chất',
 'Một ly nước cam tươi mỗi sáng là bí quyết sức khỏe đã được chứng minh qua hàng thế kỷ. Tại Lumina, chúng tôi sử dụng cam sành Vĩnh Long - giống cam có vị ngọt thanh đặc trưng của miền Tây sông nước. Mỗi ly nước cam được ép tươi nguyên chất tại chỗ, không thêm đường, không pha nước, giữ trọn vitamin C và hương vị tự nhiên.',
 'Cam sành Vĩnh Long (3-4 quả/ly), đá viên, lát cam trang trí',
 45000, 1, 4, 'https://images.unsplash.com/photo-1611162616475-46b635cb6868?w=800&q=80', NOW(), NOW()),

('Trà đào cam sả', 'Trà đào mát lạnh với cam và lá sả',
 'Trà đào cam sả là sáng tạo độc đáo của văn hóa trà Việt Nam hiện đại, xuất hiện lần đầu tại các quán trà sữa Hà Nội khoảng năm 2015 và nhanh chóng chinh phục giới trẻ cả nước. Sự kết hợp giữa trà đen đậm đà, đào ngâm ngọt dịu, cam tươi thanh mát và sả thơm lừng tạo nên thức uống giải khát hoàn hảo cho mùa hè. Tại Lumina, trà được pha từ lá trà Thái Nguyên thượng hạng.',
 'Trà đen Thái Nguyên, đào ngâm siro, cam tươi Vĩnh Long, sả cây Củ Chi, đường phèn, đá viên',
 55000, 1, 4, 'https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=800&q=80', NOW(), NOW()),

('Cà phê sữa đá', 'Cà phê phin truyền thống pha sữa đặc',
 'Cà phê sữa đá là linh hồn của văn hóa cà phê Việt Nam - quốc gia xuất khẩu cà phê lớn thứ hai thế giới. Từ những phin cà phê nhỏ nhắn trên vỉa hè Sài Gòn đến quán cóc Hà Nội, ly cà phê sữa đá luôn là người bạn đồng hành không thể thiếu của người Việt. Tại Lumina, cà phê được rang xay từ hạt Robusta Buôn Ma Thuột kết hợp Arabica Cầu Đất, pha phin chậm rãi 5-7 phút để chiết xuất trọn vẹn hương vị.',
 'Cà phê Robusta Buôn Ma Thuột, cà phê Arabica Cầu Đất, sữa đặc Ông Thọ, đá viên, phin nhôm truyền thống',
 35000, 1, 4, 'https://images.unsplash.com/photo-1578314675249-a6910f80cc4e?w=800&q=80', NOW(), NOW()),

('Bia Tiger (lon)', 'Bia Tiger 330ml lạnh',
 'Tiger Beer ra đời năm 1932 tại Singapore, nhanh chóng trở thành thương hiệu bia hàng đầu Đông Nam Á. Với hương vị cân bằng giữa malt lúa mạch và hoa bia châu Âu, Tiger là lựa chọn hoàn hảo để đi kèm với các món hải sản và đồ nướng. Tại Lumina, bia Tiger luôn được giữ lạnh ở nhiệt độ 3-5°C - nhiệt độ lý tưởng để thưởng thức.',
 'Nước, malt đại mạch, gạo, hoa bia (lon 330ml, nồng độ 5%)',
 30000, 1, 4, 'https://images.unsplash.com/photo-1608270586620-248524c67de9?w=800&q=80', NOW(), NOW()),

('Chè ba màu', 'Chè ba màu truyền thống với đậu, thạch và nước cốt dừa',
 'Chè ba màu là món tráng miệng đặc trưng miền Nam Việt Nam, phản ánh triết lý ẩm thực "ngũ sắc" trong văn hóa Á Đông. Ba lớp màu sắc tượng trưng cho sự hòa hợp: đậu đỏ đại diện cho may mắn, đậu xanh cho sức khỏe, và thạch rau câu cho sự trong sáng. Tại Lumina, chè được nấu mỗi ngày với đậu đỏ Đắk Lắk và nước cốt dừa Bến Tre thơm lừng.',
 'Đậu đỏ Đắk Lắk, đậu xanh, thạch rau câu, nước cốt dừa Bến Tre, đường phèn, đá bào',
 35000, 1, 5, 'https://images.unsplash.com/photo-1563805042-7684c8a9e9cb?w=800&q=80', NOW(), NOW()),

('Kem dừa', 'Kem dừa tươi phủ cùi dừa nạo',
 'Kem dừa là món tráng miệng mang đậm hồn Việt, gợi nhớ những vườn dừa xanh mướt của xứ Bến Tre. Vị béo ngọt tự nhiên của nước cốt dừa khi đông lạnh tạo nên viên kem mịn như lụa, tan nhẹ trên đầu lưỡi. Tại Lumina, kem dừa được làm thủ công mỗi ngày từ dừa xiêm Bến Tre, không sử dụng hương liệu nhân tạo, phủ thêm cùi dừa nạo tươi giòn sần sật.',
 'Nước cốt dừa xiêm Bến Tre, cùi dừa nạo tươi, sữa tươi, đường, vani tự nhiên Madagascar',
 45000, 1, 5, 'https://images.unsplash.com/photo-1553177595-4de2bb0842b9?w=800&q=80', NOW(), NOW()),

('Bánh tiramisu', 'Bánh tiramisu Ý với cà phê espresso',
 'Tiramisu - trong tiếng Ý có nghĩa là "nâng tôi lên" - được cho là ra đời tại nhà hàng Le Beccherie ở Treviso, Ý vào cuối những năm 1960. Sự kết hợp giữa kem mascarpone béo ngậy, bánh savoiardi thấm cà phê espresso đậm đà và lớp cacao đắng nhẹ tạo nên hương vị quyến rũ không thể cưỡng lại. Tại Lumina, tiramisu được làm tay mỗi ngày bởi pastry chef với mascarpone nhập từ Ý và espresso pha từ máy La Marzocca.',
 'Kem mascarpone Galbani (Ý), bánh savoiardi, cà phê espresso, lòng đỏ trứng gà ta, đường, rượu Marsala, bột cacao Valrhona',
 75000, 1, 5, 'https://images.unsplash.com/photo-1571115177098-24ec42ed204d?w=800&q=80', NOW(), NOW());

-- Thêm Users (password: 123456)
INSERT INTO users (id, username, password_hash, full_name, phone, email, role, created_at, updated_at, deleted) VALUES
(1, 'admin', '$2a$10$0w7tJykpZacr5NEbM.wvIeHTKViJ7ojyaB5iOEKKx/oFzkV40koMm', 'Quản trị viên', '0987654321', 'admin@restaurant.com', 'ADMIN', NOW(), NOW(), 0),
(2, 'staff', '$2a$10$0w7tJykpZacr5NEbM.wvIeHTKViJ7ojyaB5iOEKKx/oFzkV40koMm', 'Nhân viên 1', '0123456789', 'staff@restaurant.com', 'STAFF', NOW(), NOW(), 0),
(3, 'customer', '$2a$10$0w7tJykpZacr5NEbM.wvIeHTKViJ7ojyaB5iOEKKx/oFzkV40koMm', 'Khách hàng 1', '0333333333', 'customer@restaurant.com', 'CUSTOMER', NOW(), NOW(), 0),
(4, 'customer2', '$2a$10$0w7tJykpZacr5NEbM.wvIeHTKViJ7ojyaB5iOEKKx/oFzkV40koMm', 'Trần Văn Bình', '0912345678', 'binh.tran@gmail.com', 'CUSTOMER', NOW(), NOW(), 0),
(5, 'customer3', '$2a$10$0w7tJykpZacr5NEbM.wvIeHTKViJ7ojyaB5iOEKKx/oFzkV40koMm', 'Lê Thị Hương', '0977888999', 'huong.le@gmail.com', 'CUSTOMER', NOW(), NOW(), 0),
(6, 'customer4', '$2a$10$0w7tJykpZacr5NEbM.wvIeHTKViJ7ojyaB5iOEKKx/oFzkV40koMm', 'Phạm Minh Đức', '0866777888', 'duc.pham@gmail.com', 'CUSTOMER', NOW(), NOW(), 0),
(7, 'staff2', '$2a$10$0w7tJykpZacr5NEbM.wvIeHTKViJ7ojyaB5iOEKKx/oFzkV40koMm', 'Nhân viên 2', '0355666777', 'staff2@restaurant.com', 'STAFF', NOW(), NOW(), 0);

-- Cập nhật trạng thái một số bàn (Bàn 02 đang dùng, Bàn 05 đang dùng, Bàn 09 đã đặt trước)
UPDATE `tables` SET status = 'OCCUPIED', updated_at = NOW() WHERE id IN (2, 5);
UPDATE `tables` SET status = 'RESERVED', updated_at = NOW() WHERE id = 9;

-- =============================================
-- MOCK DATA: Reservations (đặt bàn)
-- =============================================
INSERT INTO reservations (id, user_id, table_id, reservation_time, expiry_datetime, number_of_guests, note, status, created_at, deleted) VALUES
-- Đặt bàn đã hoàn thành (3 ngày trước)
(1, 3, 1, DATE_SUB(NOW(), INTERVAL 3 DAY) + INTERVAL 11 HOUR, DATE_SUB(NOW(), INTERVAL 3 DAY) + INTERVAL 12 HOUR, 2, 'Kỷ niệm ngày cưới', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 4 DAY), 0),
-- Đặt bàn đã hoàn thành (2 ngày trước)
(2, 4, 4, DATE_SUB(NOW(), INTERVAL 2 DAY) + INTERVAL 18 HOUR, DATE_SUB(NOW(), INTERVAL 2 DAY) + INTERVAL 19 HOUR, 5, 'Tiệc sinh nhật', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 3 DAY), 0),
-- Đặt bàn đã hoàn thành (hôm qua)
(3, 5, 7, DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 12 HOUR, DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 13 HOUR, 4, NULL, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 2 DAY), 0),
-- Đặt bàn đã hoàn thành (hôm qua)
(4, 6, 3, DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 19 HOUR, DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 20 HOUR, 3, 'Ăn tối gia đình', 'COMPLETED', DATE_SUB(NOW(), INTERVAL 2 DAY), 0),
-- Đặt bàn đã xác nhận (hôm nay - đang phục vụ)
(5, 3, 2, NOW() - INTERVAL 1 HOUR, NOW() + INTERVAL 1 HOUR, 2, 'Bàn gần cửa sổ', 'CONFIRMED', DATE_SUB(NOW(), INTERVAL 1 DAY), 0),
-- Đặt bàn đã xác nhận (hôm nay - đang phục vụ)
(6, 4, 5, NOW() - INTERVAL 30 MINUTE, NOW() + INTERVAL 90 MINUTE, 2, NULL, 'CONFIRMED', NOW() - INTERVAL 2 HOUR, 0),
-- Đặt bàn chờ xác nhận (hôm nay chiều)
(7, 5, 9, NOW() + INTERVAL 3 HOUR, NOW() + INTERVAL 4 HOUR, 4, 'Họp bạn bè', 'PENDING', NOW() - INTERVAL 1 HOUR, 0),
-- Đặt bàn chờ xác nhận (ngày mai)
(8, 6, 6, DATE_ADD(NOW(), INTERVAL 1 DAY) + INTERVAL 19 HOUR, DATE_ADD(NOW(), INTERVAL 1 DAY) + INTERVAL 20 HOUR, 3, 'Tiệc công ty nhỏ', 'PENDING', NOW(), 0),
-- Đặt bàn đã hủy
(9, 3, 8, DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 20 HOUR, DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 21 HOUR, 6, 'Đã hủy do thay đổi kế hoạch', 'CANCELLED', DATE_SUB(NOW(), INTERVAL 3 DAY), 0),
-- Đặt bàn chờ xác nhận (hôm nay tối)
(10, 4, 10, NOW() + INTERVAL 5 HOUR, NOW() + INTERVAL 6 HOUR, 5, 'Xin bàn yên tĩnh', 'PENDING', NOW() - INTERVAL 30 MINUTE, 0);

-- =============================================
-- MOCK DATA: Orders (đơn hàng)
-- =============================================
INSERT INTO orders (id, table_id, user_id, reservation_id, total_amount, amount_paid, status, created_at, updated_at) VALUES
-- Đơn đã hoàn thành (3 ngày trước - từ đặt bàn #1)
(1, 1, 2, 1, 375000.00, 375000.00, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 3 DAY) + INTERVAL 11 HOUR, DATE_SUB(NOW(), INTERVAL 3 DAY) + INTERVAL 13 HOUR),
-- Đơn đã hoàn thành (2 ngày trước - từ đặt bàn #2)
(2, 4, 2, 2, 895000.00, 895000.00, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 2 DAY) + INTERVAL 18 HOUR, DATE_SUB(NOW(), INTERVAL 2 DAY) + INTERVAL 20 HOUR),
-- Đơn đã hoàn thành (hôm qua - từ đặt bàn #3)
(3, 7, 7, 3, 620000.00, 620000.00, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 12 HOUR, DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 14 HOUR),
-- Đơn đã hoàn thành (hôm qua - từ đặt bàn #4)
(4, 3, 2, 4, 510000.00, 510000.00, 'COMPLETED', DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 19 HOUR, DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 21 HOUR),
-- Đơn đã hoàn thành (hôm nay sáng - khách walk-in, không đặt trước)
(5, 6, 2, NULL, 285000.00, 285000.00, 'COMPLETED', NOW() - INTERVAL 4 HOUR, NOW() - INTERVAL 2 HOUR),
-- Đơn đang phục vụ (hôm nay - từ đặt bàn #5, bàn 02)
(6, 2, 2, 5, 430000.00, 100000.00, 'SERVING', NOW() - INTERVAL 1 HOUR, NOW() - INTERVAL 30 MINUTE),
-- Đơn đang phục vụ (hôm nay - từ đặt bàn #6, bàn 05)
(7, 5, 7, 6, 245000.00, 0.00, 'PENDING', NOW() - INTERVAL 30 MINUTE, NOW() - INTERVAL 20 MINUTE),
-- Đơn đã hủy (từ đặt bàn bị hủy #9)
(8, 8, 2, 9, 0.00, 0.00, 'CANCELLED', DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 20 HOUR, DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 20 HOUR);

-- =============================================
-- MOCK DATA: Order Items (chi tiết món ăn trong đơn)
-- =============================================
INSERT INTO order_items (id, order_id, menu_item_id, quantity, price_at_time, note) VALUES
-- Đơn #1: Gỏi cuốn (45k x2) + Bò lúc lắc (145k x1) + Cà phê (35k x2) + Sinh tố bơ (55k x1) = 360k -> set 375k (round)
(1, 1, 1, 2, 45000.00, NULL),
(2, 1, 5, 1, 145000.00, 'Chín kỹ'),
(3, 1, 16, 2, 35000.00, NULL),
(4, 1, 13, 1, 55000.00, 'Ít đường'),
-- Đơn #2: Lẩu thái (285k x1) + Tôm hùm (650k x1) = 935k -> set 895k (có giảm tiệc)
(5, 2, 8, 1, 285000.00, 'Cay vừa'),
(6, 2, 9, 1, 650000.00, NULL),
(7, 2, 15, 3, 55000.00, NULL),
(8, 2, 17, 5, 30000.00, 'Lạnh'),
-- Đơn #3: Cơm chiên (85k x2) + Mì xào (115k x2) + Cua rang me (320k x1) = 720k -> set 620k
(9, 3, 4, 2, 85000.00, NULL),
(10, 3, 6, 2, 115000.00, NULL),
(11, 3, 10, 1, 320000.00, 'Sốt me nhiều'),
(12, 3, 14, 2, 45000.00, NULL),
-- Đơn #4: Cơm tấm (95k x2) + Salad (75k x1) + Cá hồi (195k x1) + Kem dừa (45k x2) = 550k -> set 510k
(13, 4, 7, 2, 95000.00, NULL),
(14, 4, 3, 1, 75000.00, NULL),
(15, 4, 12, 1, 195000.00, 'Chín tái'),
(16, 4, 19, 2, 45000.00, NULL),
-- Đơn #5: Lẩu thái (285k x1)
(17, 5, 8, 1, 285000.00, 'Cay nhiều'),
(18, 5, 15, 4, 55000.00, NULL),
-- Đơn #6: Bò lúc lắc (145k x1) + Mực nướng (165k x1) + Bia (30k x4) = 430k
(19, 6, 5, 1, 145000.00, NULL),
(20, 6, 11, 1, 165000.00, NULL),
(21, 6, 17, 4, 30000.00, 'Lạnh'),
-- Đơn #7: Gỏi cuốn (45k x2) + Cơm chiên (85k x1) + Trà đào (55k x1) + Chè ba màu (35k x1) = 265k -> set 245k
(22, 7, 1, 2, 45000.00, NULL),
(23, 7, 4, 1, 85000.00, 'Không hành'),
(24, 7, 15, 1, 55000.00, NULL),
(25, 7, 18, 1, 35000.00, NULL);

-- =============================================
-- MOCK DATA: Payments (thanh toán - chỉ cho đơn đã COMPLETED)
-- =============================================
INSERT INTO payments (id, order_id, amount, method, status, created_at, updated_at) VALUES
-- Thanh toán đơn #1 (3 ngày trước - tiền mặt)
(1, 1, 375000.00, 'CASH', 'PAID', DATE_SUB(NOW(), INTERVAL 3 DAY) + INTERVAL 13 HOUR, DATE_SUB(NOW(), INTERVAL 3 DAY) + INTERVAL 13 HOUR),
-- Thanh toán đơn #2 (2 ngày trước - chuyển khoản)
(2, 2, 895000.00, 'BANK_TRANSFER', 'PAID', DATE_SUB(NOW(), INTERVAL 2 DAY) + INTERVAL 20 HOUR, DATE_SUB(NOW(), INTERVAL 2 DAY) + INTERVAL 20 HOUR),
-- Thanh toán đơn #3 (hôm qua - thẻ)
(3, 3, 620000.00, 'CARD', 'PAID', DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 14 HOUR, DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 14 HOUR),
-- Thanh toán đơn #4 (hôm qua - tiền mặt)
(4, 4, 510000.00, 'CASH', 'PAID', DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 21 HOUR, DATE_SUB(NOW(), INTERVAL 1 DAY) + INTERVAL 21 HOUR),
-- Thanh toán đơn #5 (hôm nay - ví điện tử)
(5, 5, 285000.00, 'E_WALLET', 'PAID', NOW() - INTERVAL 2 HOUR, NOW() - INTERVAL 2 HOUR);
