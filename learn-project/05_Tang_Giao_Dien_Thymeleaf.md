# Bài 5: Tầng Giao diện (Thymeleaf View)

Tầng giao diện nằm hoàn toàn trong thư mục `src/main/resources/`. Nơi đây chứa các tài nguyên (Resources) sẽ hiển thị trực tiếp lên màn hình của khách hàng.

## 1. Cấu trúc thư mục Resources
*   **`static/`**: Chứa các file "tĩnh" (Không đổi theo dữ liệu DB). Bao gồm các file CSS (Làm đẹp web), JS (Hiệu ứng click), và Hình ảnh tĩnh.
*   **`templates/`**: Chứa các file HTML "động" (Sử dụng công cụ Thymeleaf). Mọi file bạn thấy ở đây (như `admin/order/index.html`) sẽ không thể mở trực tiếp bằng cách nhấp đúp trên máy tính được, vì bên trong nó chứa các cú pháp nhúng của Java.

## 2. Thymeleaf là gì và hoạt động ra sao?

Nhớ lại Bài 4, khi Controller ném dữ liệu vào `Model` mang tên "orders" rồi trả về file HTML. Làm sao file HTML có thể tự vẽ ra cái bảng (Table) gồm 10 hàng cho 10 hóa đơn? Đó là nhờ **Thymeleaf**.

Thymeleaf cung cấp các thuộc tính bắt đầu bằng tiền tố `th:` chèn thẳng vào thẻ HTML.

### Các "Phép thuật" Thymeleaf cốt lõi trong dự án:

#### 1. In dữ liệu ra màn hình (`th:text`)
Nếu Model có biến tên là `username` mang giá trị "vincent":
```html
Xin chào, <span th:text="${username}">Tên mặc định</span>
<!-- Kết quả khi chạy lên web: Xin chào, vincent -->
```

#### 2. Vòng lặp in danh sách (`th:each`)
Đây là cú pháp được dùng nhiều nhất ở các màn hình Danh sách (Ví dụ danh sách Bàn ăn, danh sách Khách hàng).
```html
<tbody>
    <tr th:each="order : ${orders}">
        <td th:text="${order.id}">1</td>
        <td th:text="${order.table.tableName}">Bàn 01</td>
        <td th:text="${order.totalAmount}">500000</td>
    </tr>
</tbody>
```
Đoạn code trên giống hệt vòng lặp `for(Order order : orders)` trong Java. Nó sẽ copy thẻ `<tr>` ra N lần (bằng số lượng phần tử trong danh sách) và nhét dữ liệu ID, Tên Bàn, Tổng Tiền vào từng dòng tương ứng.

#### 3. Cấu trúc điều kiện (`th:if` / `th:unless`)
Giúp Ẩn/Hiện các thành phần trên web. Rất hay dùng để tô màu hoặc hiện các nút bấm.
```html
<span th:if="${order.status.name() == 'PENDING'}" class="badge bg-warning">Chờ xử lý</span>
<span th:if="${order.status.name() == 'COMPLETED'}" class="badge bg-success">Đã thanh toán</span>
```
Đoạn này kiểm tra trạng thái hóa đơn. Nếu `PENDING` thì nhúng đoạn HTML có nền vàng (`bg-warning`). Nếu `COMPLETED` thì in thẻ HTML nền xanh.

#### 4. Kế thừa giao diện (`th:replace` / Lấy Layout)
Trong thư mục `templates/layout`, bạn sẽ thấy các file như `admin_layout.html`. Chúng đóng vai trò làm cái Khung (Khung chứa Sidebar, Header cố định). Các trang con (như Quản lý Bàn, Quản lý Khách) chỉ cần "nhúng" nội dung ruột của mình vào cái Khung đó. Điều này giúp không phải copy-paste cục Sidebar lặp đi lặp lại ở 50 file HTML khác nhau!

---

## 💡 Tổng kết cả chuỗi 5 Bài viết:
Chúc mừng bạn! Nếu bạn nắm vững 5 bài viết này, bạn hoàn toàn có khả năng:
1.  **Dò lỗi (Debug):** Nếu giao diện web sai -> Sửa file HTML ở bài 5. Nếu báo lỗi quyền truy cập -> Sửa Security Bài 4. Nếu logic tính tiền sai -> Cắm đầu vào Service Bài 3 tìm!
2.  **Mở rộng thêm tính năng:** Bắt đầu bằng việc Tạo thêm 1 bảng mới (Entity) -> Tạo Thủ Kho (Repository) -> Tạo Bếp trưởng xử lý logic (Service) -> Tạo Tiếp tân mở đường dẫn (Controller) -> Cuối cùng là gõ file Thymeleaf cho khách xem!
