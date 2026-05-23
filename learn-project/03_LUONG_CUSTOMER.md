# 🍽️ LUỒNG NGHIỆP VỤ CUSTOMER (Khách hàng)

## Tổng quan chức năng

Khách hàng sau khi đăng nhập có thể:
1. **Xem thực đơn** → Thêm món vào giỏ hàng
2. **Quản lý giỏ hàng** → Xem, cập nhật, xóa món
3. **Đặt bàn** → Chọn bàn, ngày giờ, số khách
4. **Xem lịch sử đặt bàn** → Hủy đặt bàn nếu cần

---

## 1. Sơ đồ tổng thể chức năng Customer

```mermaid
flowchart TD
    A["👤 Customer đăng nhập"] --> B["🏠 Trang chủ (/)"]
    
    B --> C["📋 Xem Thực đơn (/menu)"]
    B --> D["🪑 Đặt bàn (/reservation)"]
    B --> E["📜 Lịch sử đặt bàn (/my-reservations)"]
    
    C --> F["🛒 Thêm vào giỏ hàng (POST /cart/add)"]
    F --> G["🛒 Xem giỏ hàng (/cart)"]
    G --> H["✏️ Cập nhật số lượng (POST /cart/update)"]
    G --> I["🗑️ Xóa món (/cart/remove/{id})"]
    
    D --> J["📝 Chọn bàn + Ngày giờ + Gửi đặt bàn"]
    J --> K["✅ Đặt bàn thành công → /my-reservations"]
    J --> L["❌ Bàn bị trùng (Optimistic Lock) → Chọn lại"]
    
    E --> M["🚫 Hủy đặt bàn (POST /reservation/cancel)"]
```

---

## 2. Luồng xem thực đơn & thêm giỏ hàng

```mermaid
sequenceDiagram
    actor Customer as 👤 Khách hàng
    participant Browser as 🌐 Trình duyệt
    participant Ctrl as 📋 CustomerController
    participant MenuSrv as ⚙️ MenuItemService
    participant CartSrv as 🛒 CartService

    Customer->>Browser: Bấm "Xem thực đơn"
    Browser->>Ctrl: GET /menu
    Ctrl->>MenuSrv: findAll() → Lấy danh sách món
    Ctrl->>CartSrv: getCount() → Số lượng trong giỏ
    Ctrl-->>Browser: Trả trang menu.html + danh sách món

    Customer->>Browser: Chọn món → Bấm "Thêm vào giỏ"
    Browser->>Ctrl: POST /cart/add (menuItemId, quantity)
    Ctrl->>MenuSrv: findById(menuItemId)
    Ctrl->>CartSrv: add(CartItem) → Lưu vào Session
    Ctrl-->>Browser: redirect → /menu + Thông báo "Đã thêm"
```

### Code thực tế:

```java
// CustomerController.java
@PostMapping("/cart/add")
public String addToCart(@RequestParam Long menuItemId,
                        @RequestParam(defaultValue = "1") Integer quantity,
                        RedirectAttributes redirectAttrs) {
    MenuItem item = menuItemService.findById(menuItemId).orElseThrow();
    CartItem cartItem = CartItem.builder()
            .menuItemId(item.getId())
            .name(item.getName())
            .price(item.getPrice())
            .imageUrl(item.getImageUrl())
            .quantity(quantity)
            .build();
    cartService.add(cartItem);
    redirectAttrs.addFlashAttribute("success", "Đã thêm " + item.getName() + " vào giỏ hàng");
    return "redirect:/menu";
}
```

> 💡 **Giỏ hàng được lưu trong Session** (không lưu DB). Khi đóng trình duyệt → giỏ hàng mất.

---

## 3. Luồng đặt bàn chi tiết

```mermaid
sequenceDiagram
    actor Customer as 👤 Khách hàng
    participant Browser as 🌐 Trình duyệt
    participant Ctrl as 📋 CustomerController
    participant TableSrv as 🪑 DiningTableService
    participant ResSrv as 📅 ReservationService
    participant DB as 💾 Database

    Customer->>Browser: Bấm "Đặt bàn"
    Browser->>Ctrl: GET /reservation
    Ctrl->>TableSrv: findAll() → Danh sách bàn
    Ctrl-->>Browser: Trả trang reservation.html + danh sách bàn

    Customer->>Browser: Chọn bàn, ngày, giờ, số khách → Bấm "Đặt bàn"
    Browser->>Ctrl: POST /reservation (tableId, date, time, guests, note)

    Ctrl->>ResSrv: createReservation(userId, tableId, dateTime, guests, note)
    
    ResSrv->>DB: Kiểm tra bàn có trống không (Optimistic Locking)
    
    alt ✅ Bàn còn trống
        ResSrv->>DB: INSERT reservation + UPDATE table status → RESERVED
        ResSrv-->>Ctrl: Đặt thành công
        Ctrl-->>Browser: redirect → /my-reservations + "Đặt bàn thành công!"
    else ❌ Bàn vừa bị người khác đặt (Race Condition)
        ResSrv-->>Ctrl: Throw ObjectOptimisticLockingFailureException
        Ctrl-->>Browser: redirect → /reservation + "Bàn vừa có người đặt!"
    end
```

### Optimistic Locking là gì?

Khi 2 khách hàng cùng lúc đặt chung 1 bàn:
- Spring Data JPA dùng trường `@Version` trong entity `DiningTable`
- Người đặt **trước** thành công → bàn chuyển trạng thái `RESERVED`
- Người đặt **sau** → JPA phát hiện version đã thay đổi → throw exception → hiện thông báo lỗi

---

## 4. Luồng xem & hủy đặt bàn

```mermaid
sequenceDiagram
    actor Customer as 👤 Khách hàng
    participant Browser as 🌐 Trình duyệt
    participant Ctrl as 📋 CustomerController
    participant ResSrv as 📅 ReservationService
    participant DB as 💾 Database

    Customer->>Browser: Bấm "Lịch sử đặt bàn"
    Browser->>Ctrl: GET /my-reservations
    Ctrl->>ResSrv: findByUserId(userId)
    ResSrv->>DB: SELECT * FROM reservations WHERE user_id = ?
    Ctrl-->>Browser: Trả trang my-reservations.html + danh sách

    Customer->>Browser: Bấm "Hủy đặt bàn" trên 1 đặt bàn
    Browser->>Ctrl: POST /reservation/cancel (reservationId)
    Ctrl->>ResSrv: cancelReservation(reservationId, userId)
    
    Note over ResSrv: Cập nhật reservation status → CANCELLED<br/>Cập nhật bàn status → AVAILABLE
    
    ResSrv->>DB: UPDATE reservations SET status='CANCELLED'
    ResSrv->>DB: UPDATE tables SET status='AVAILABLE'
    Ctrl-->>Browser: redirect → /my-reservations + "Đã hủy thành công"
```

---

## 5. Bảng tóm tắt các endpoint Customer

| HTTP Method | URL | Controller Method | Mô tả |
|------------|-----|------------------|--------|
| GET | `/menu` | `viewMenu()` | Xem thực đơn |
| POST | `/cart/add` | `addToCart()` | Thêm món vào giỏ |
| GET | `/cart` | `viewCart()` | Xem giỏ hàng |
| POST | `/cart/update` | `updateCart()` | Cập nhật số lượng |
| GET | `/cart/remove/{id}` | `removeFromCart()` | Xóa món khỏi giỏ |
| GET | `/reservation` | `viewReservationForm()` | Form đặt bàn |
| POST | `/reservation` | `submitReservation()` | Gửi đặt bàn |
| GET | `/my-reservations` | `myReservations()` | Lịch sử đặt bàn |
| POST | `/reservation/cancel` | `cancelReservation()` | Hủy đặt bàn |
