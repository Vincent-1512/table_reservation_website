# 🔄 LUỒNG XỬ LÝ REQUEST TỔNG HỢP (MVC Pattern)

## 1. Request đi qua những lớp nào?

Khi người dùng bấm một nút hoặc truy cập một URL, request sẽ đi qua các tầng theo thứ tự sau:

```mermaid
flowchart TD
    A["🌐 Trình duyệt gửi HTTP Request"] --> B["🔒 Spring Security Filter Chain"]
    
    B --> C{Kiểm tra quyền}
    C -->|Không đủ quyền| D["⛔ 403 hoặc redirect /login"]
    C -->|OK| E["📋 Controller"]
    
    E --> F["⚙️ Service"]
    F --> G["💾 Repository (JPA)"]
    G --> H[("🗄️ MySQL Database")]
    
    H --> G
    G --> F
    F --> E
    
    E --> I["📄 Trả tên view (String)"]
    I --> J["🎨 Thymeleaf Template Engine"]
    J --> K["📄 Render HTML"]
    K --> L["🌐 Trình duyệt nhận HTML Response"]
```

---

## 2. Ví dụ cụ thể: Staff xem chi tiết Order

Giả sử nhân viên bấm vào **"Xem Order"** trên bàn 01:

```mermaid
sequenceDiagram
    actor Staff as 👨‍🍳 Nhân viên
    participant Browser as 🌐 Trình duyệt
    participant Security as 🔒 Security Filter
    participant Ctrl as 📋 StaffOrderController
    participant OrderSrv as ⚙️ OrderService
    participant ItemSrv as 🍕 OrderItemService
    participant MenuSrv as 📋 MenuItemService
    participant PaySrv as 💳 PaymentService
    participant Repo as 💾 Repository
    participant DB as 🗄️ MySQL
    participant Thyme as 🎨 Thymeleaf

    Staff->>Browser: Bấm "Xem Order →" trên bàn 01
    Browser->>Security: GET /staff/orders/5

    Note over Security: 1. Kiểm tra đã login chưa? ✅<br/>2. URL /staff/** → cần ROLE_STAFF hoặc ADMIN ✅
    
    Security->>Ctrl: Cho phép truy cập

    Note over Ctrl: @GetMapping("/{id}")<br/>orderDetail(@PathVariable Long id, Model model)

    Ctrl->>OrderSrv: findById(5)
    OrderSrv->>Repo: orderRepository.findById(5)
    Repo->>DB: SELECT * FROM orders WHERE id = 5
    DB-->>Repo: Order object
    Repo-->>OrderSrv: Optional<Order>
    OrderSrv-->>Ctrl: Order

    Ctrl->>ItemSrv: findByOrderId(5)
    ItemSrv->>Repo: orderItemRepository.findByOrderId(5)
    Repo->>DB: SELECT * FROM order_items WHERE order_id = 5
    DB-->>Repo: List<OrderItem>
    Repo-->>ItemSrv: List<OrderItem>
    ItemSrv-->>Ctrl: List<OrderItem>

    Ctrl->>MenuSrv: findAll()
    MenuSrv->>DB: SELECT * FROM menu_items
    DB-->>MenuSrv: List<MenuItem>
    MenuSrv-->>Ctrl: List<MenuItem>

    Ctrl->>PaySrv: findByOrderId(5)
    PaySrv->>DB: SELECT * FROM payments WHERE order_id = 5
    DB-->>PaySrv: Optional<Payment>
    PaySrv-->>Ctrl: Payment (hoặc null)

    Note over Ctrl: model.addAttribute("order", order)<br/>model.addAttribute("orderItems", items)<br/>model.addAttribute("menuItems", allMenu)<br/>model.addAttribute("payment", payment)<br/>return "staff/order/detail"

    Ctrl->>Thyme: Tên view = "staff/order/detail"
    Note over Thyme: Tìm file:<br/>templates/staff/order/detail.html<br/>Thay th:text, th:each, th:if<br/>bằng dữ liệu từ Model

    Thyme-->>Browser: HTML hoàn chỉnh
    Browser-->>Staff: Hiển thị trang chi tiết Order
```

---

## 3. Ví dụ cụ thể: POST request (Thêm món vào Order)

```mermaid
sequenceDiagram
    actor Staff as 👨‍🍳 Nhân viên
    participant Browser as 🌐 Trình duyệt
    participant Security as 🔒 Security Filter
    participant Ctrl as 📋 StaffOrderController
    participant ItemSrv as 🍕 OrderItemService
    participant DB as 🗄️ MySQL

    Staff->>Browser: Chọn "Phở bò" (menuItemId=3) x2 → Bấm "Thêm món"
    
    Note over Browser: Form submit:<br/>POST /staff/orders/5/add-item<br/>menuItemId=3, quantity=2, note="Ít hành"

    Browser->>Security: POST /staff/orders/5/add-item + CSRF token
    
    Note over Security: 1. Check CSRF token ✅<br/>2. Check login ✅<br/>3. Check role ✅

    Security->>Ctrl: Cho phép truy cập

    Note over Ctrl: @PostMapping("/{id}/add-item")<br/>addItem(id=5, menuItemId=3, quantity=2, note="Ít hành")

    Ctrl->>ItemSrv: addMenuItemToOrder(5, 3, 2, "Ít hành")
    
    Note over ItemSrv: 1. Tìm MenuItem id=3 → lấy price = 50.000đ<br/>2. Tạo OrderItem(orderId=5, menuItemId=3,<br/>   quantity=2, unitPrice=50.000, note="Ít hành")<br/>3. Cập nhật Order totalAmount += 100.000đ

    ItemSrv->>DB: INSERT order_item + UPDATE order.total_amount
    ItemSrv-->>Ctrl: OK

    Note over Ctrl: return "redirect:/staff/orders/5"

    Ctrl-->>Browser: HTTP 302 Redirect → /staff/orders/5
    Browser->>Ctrl: GET /staff/orders/5 (tải lại trang)
    Ctrl-->>Browser: Trang detail.html đã cập nhật (có thêm Phở bò x2)
```

> 💡 **Mẫu PRG (Post-Redirect-Get)**: Sau mỗi POST, controller luôn trả về `redirect:` thay vì trả view trực tiếp. Điều này tránh việc user bấm F5 (refresh) gửi lại form.

---

## 4. Bảng tổng hợp: Component nào làm gì?

| Tầng | Component | Nhiệm vụ | Ví dụ |
|------|-----------|----------|-------|
| **Trình duyệt** | HTML Form | Gửi request đến server | `<form action="/staff/orders/5/add-item" method="post">` |
| **Security** | SecurityFilterChain | Kiểm tra login + role + CSRF | Chặn `/admin/**` nếu không phải ADMIN |
| **Controller** | `@Controller` | Nhận request, gọi service, trả view | `StaffOrderController.addItem()` |
| **Service** | `@Service` | Xử lý logic nghiệp vụ | Tính tổng tiền, kiểm tra trạng thái |
| **Repository** | `@Repository` (JPA) | Giao tiếp với DB | `orderRepository.findById(5)` |
| **Entity** | `@Entity` | Ánh xạ bảng trong DB | `Order`, `OrderItem`, `Payment` |
| **View** | Thymeleaf `.html` | Render HTML từ dữ liệu Model | `th:each="item : ${orderItems}"` |

---

## 5. Annotation thường dùng

| Annotation | Ý nghĩa | Ví dụ |
|-----------|---------|-------|
| `@Controller` | Đánh dấu class là Controller | `public class StaffOrderController` |
| `@RequestMapping("/staff/orders")` | Prefix cho tất cả URL trong controller | Mọi method trong class sẽ bắt đầu bằng `/staff/orders` |
| `@GetMapping("/{id}")` | Bắt request GET | Hiển thị chi tiết order |
| `@PostMapping("/{id}/add-item")` | Bắt request POST | Thêm món (thay đổi dữ liệu) |
| `@PathVariable` | Lấy giá trị từ URL | `/staff/orders/5` → `id = 5` |
| `@RequestParam` | Lấy giá trị từ form/query | `?status=PENDING` → `status = PENDING` |
| `@Service` | Đánh dấu class chứa logic nghiệp vụ | `OrderServiceImpl` |
| `@Repository` | Đánh dấu interface giao tiếp DB | `OrderRepository extends JpaRepository` |
| `@Entity` | Đánh dấu class ánh xạ bảng DB | `Order` → bảng `orders` |
| `@Transactional` | Đảm bảo toàn vẹn dữ liệu | Nếu 1 bước lỗi → rollback tất cả |
| `RedirectAttributes` | Truyền thông báo qua redirect | `addFlashAttribute("success", "Thành công!")` |

---

## 6. Thymeleaf cơ bản

| Cú pháp | Mô tả | Ví dụ |
|---------|-------|-------|
| `th:text` | Hiển thị text | `<span th:text="${order.totalAmount}">0đ</span>` |
| `th:each` | Vòng lặp | `<tr th:each="item : ${orderItems}">` |
| `th:if` | Điều kiện hiển thị | `<div th:if="${order.status.name() == 'PENDING'}">` |
| `th:unless` | Ngược lại th:if | Hiển thị khi điều kiện sai |
| `th:action` | URL cho form | `th:action="@{'/staff/orders/' + ${order.id} + '/pay'}"` |
| `th:href` | URL cho link | `th:href="@{'/staff/orders/' + ${order.id}}"` |
| `th:replace` | Chèn fragment | `th:replace="~{fragments/staff-layout :: sidebar}"` |
| `@{...}` | Tạo URL tương đối | `@{/staff/orders}` → `/staff/orders` |
| `${...}` | Lấy biến từ Model | `${order.totalAmount}` |
