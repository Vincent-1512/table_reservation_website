# Bài 1: Kiến trúc Tổng quan và Luồng chạy của Hệ thống

Xin chào Developer! 
Để hiểu toàn bộ đống code mà chúng ta đang có, bạn cần phải nhìn dự án này từ trên cao xuống. Hãy tưởng tượng dự án như một nhà hàng ngoài đời thực, chúng ta sẽ xem ai làm việc gì nhé.

## 1. Cấu trúc thư mục cốt lõi (Mô hình 3 lớp - 3 Tier Architecture)

Spring Boot sử dụng mô hình MVC (Model - View - Controller), nhưng chia nhỏ ra thành các tầng (Layer) để code không bị dính chùm vào nhau. Mọi code Java đều nằm trong `src/main/java/vn/edu/ptit/restaurant`.

Dưới đây là các "phòng ban" trong nhà hàng của bạn:

| Thư mục / Tầng (Layer) | Vai trò trong nhà hàng thực tế | Giải thích chuyên môn (Dev) |
| :--- | :--- | :--- |
| **`entity`** (Model) | **Khu vực chứa nguyên liệu/Kho:** Bản vẽ mô tả Bàn, Món Ăn, Hóa Đơn có hình thù ra sao. | Định nghĩa cấu trúc các bảng trong Database. Mỗi class ở đây (VD: `User`, `Order`) sẽ tương ứng với 1 bảng trong MySQL. |
| **`repository`** | **Thủ kho:** Người duy nhất có chìa khóa vào kho (Database) để lấy/cất nguyên liệu. | Chứa các Interface kế thừa `JpaRepository`. Đây là nơi chứa code gọi thẳng xuống Database để `SELECT`, `INSERT`, `UPDATE`, `DELETE`. Tầng khác muốn lấy dữ liệu BẮT BUỘC phải thông qua đây. |
| **`service`** | **Bếp trưởng:** Nhận nguyên liệu từ thủ kho, chế biến theo công thức, nấu chín. | Tầng chứa **Logic Nghiệp vụ (Business Logic)**. Ví dụ: Tính tổng tiền hóa đơn, kiểm tra bàn trống hay không. Nó gọi `Repository` để lấy data, xử lý data đó, rồi trả lên trên. |
| **`controller`** | **Phục vụ bàn:** Đứng ở ngoài sảnh, lắng nghe yêu cầu của khách, chạy vào bếp báo Bếp trưởng, rồi mang đồ ăn ra cho khách. | Nơi tiếp nhận Request (URL/HTTP) từ trình duyệt. Gọi `Service` để lấy dữ liệu đã chế biến, nhét dữ liệu đó vào một file giao diện HTML, rồi ném HTML đó về cho trình duyệt. |
| **`resources/templates`** (View) | **Mặt tiền nhà hàng:** Cái bàn, cái ghế, menu in giấy để khách nhìn thấy. | Chứa các file HTML có nhúng cú pháp Thymeleaf (`th:text`, `th:each`) để hiển thị giao diện Web ra cho người dùng thao tác. |

---

## 2. Các thư mục hỗ trợ khác
Bên cạnh 3 lớp chính, bạn sẽ thấy các thư mục phụ trợ:
*   **`config`**: Nơi chứa các cấu hình hệ thống (như cấu hình Cloudinary để up ảnh, cấu hình Web).
*   **`security`**: Đội bảo vệ! File `SecurityConfig.java` và `CustomUserDetailsService.java` nằm ở đây. Bảo vệ sẽ kiểm tra xem ai được vào cửa, ai phải đăng nhập, ai là Admin, ai là Khách.
*   **`dto` (Data Transfer Object)**: Hộp cơm Bento. Đôi khi bạn không muốn bê nguyên một con lợn (`Entity` to bự) từ kho ra cho khách, bạn chỉ muốn lấy vài miếng thịt bỏ vào hộp Bento. `DTO` dùng để chứa dữ liệu trung gian (VD: `CartItem` - chứa thông tin tạm thời của giỏ hàng).
*   **`exception`**: Đội cứu thương. Nơi xử lý tập trung các lỗi (Lỗi 404, lỗi không tìm thấy bàn...).

---

## 3. Theo dõi 1 Request thực tế chạy qua các tầng

Để hiểu rõ cách các file "nói chuyện" với nhau, chúng ta hãy xem kịch bản: **Khách hàng bấm vào menu "Đồ Uống" trên Website.**

### Bước 1: Trình duyệt gửi Request
Khách hàng truy cập URL: `http://localhost:8081/menu?categoryId=4` (ID 4 là Thức uống).

### Bước 2: `CustomerController` đón khách
Trong `src/main/java/.../controller/CustomerController.java`, hàm `menu()` sẽ đón cái request này. Nó thấy `categoryId=4`. Nhưng Controller không tự móc Database, nó phải nhờ Bếp trưởng (`Service`).
```java
// Bên trong CustomerController
List<MenuItem> menuItems = menuItemService.findByCategoryId(4);
```

### Bước 3: `MenuItemServiceImpl` xử lý logic
Tầng Service (Bếp trưởng) trong `src/main/java/.../service/impl/MenuItemServiceImpl.java` nhận lệnh. Nó sẽ kiểm tra xem ID 4 có hợp lệ không. Sau đó nó bảo Thủ kho (`Repository`) đi lấy đồ.
```java
// Bên trong MenuItemServiceImpl
return menuItemRepository.findByCategoryIdAndIsAvailableTrue(4);
```

### Bước 4: `MenuItemRepository` gọi thẳng xuống MySQL
Trong `src/main/java/.../repository/MenuItemRepository.java`. Ở đây thực chất không có code Java, chỉ có một khai báo hàm. Spring Data JPA (Framework) tự động dịch hàm đó thành câu lệnh SQL:
```sql
SELECT * FROM menu_items WHERE category_id = 4 AND is_available = 1;
```
Dữ liệu được móc lên từ Database, ép thành danh sách các đối tượng `MenuItem` (thuộc tầng `entity`).

### Bước 5: Trả ngược lên và Render Giao diện
*   **Thủ kho** ném danh sách món ăn cho **Bếp trưởng**.
*   **Bếp trưởng** ném danh sách đó cho **Phục vụ** (`Controller`).
*   **Phục vụ** nhét danh sách đó vào cái túi nilon tên là `Model`, và dán nhãn "menu/index" (Chính là file giao diện).
```java
model.addAttribute("menuItems", menuItems);
return "customer/menu"; // Trả về trang HTML
```
Cuối cùng, file `src/main/resources/templates/customer/menu.html` sẽ dùng vòng lặp Thymeleaf `th:each="item : ${menuItems}"` để in ra hình ảnh và giá tiền của từng cốc sinh tố lên màn hình máy tính của khách!

---
> [!NOTE]  
> **Tóm tắt quy tắc bất di bất dịch của kiến trúc này:**
> *   `Controller` KHÔNG BAO GIỜ được gọi trực tiếp `Repository` (Phục vụ không được tự ý vào kho lấy đồ). Nó BẮT BUỘC phải gọi qua `Service`.
> *   `Entity` là trái tim, mọi tầng đều dùng nó để chứa dữ liệu.
> *   Luồng dữ liệu chuẩn: **Trình duyệt ↔ Controller ↔ Service ↔ Repository ↔ Database**.
