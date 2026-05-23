# 👨‍🍳 LUỒNG NGHIỆP VỤ STAFF (Nhân viên)

## Tổng quan chức năng

Nhân viên sau khi đăng nhập được chuyển tới `/staff/orders` và có thể:
1. **Dashboard** → Tổng quan ca làm (doanh thu, đơn đang xử lý)
2. **Sơ đồ bàn** → Xem trạng thái bàn, mở bàn
3. **Quản lý Order** → Mở bàn, gọi món, thanh toán, đóng bàn, hủy bàn
4. **Quản lý đặt bàn** → Xác nhận / Hủy đặt bàn của khách
5. **Lịch sử hóa đơn** → Xem các hóa đơn đã thanh toán
6. **Trang cá nhân** → Đổi thông tin cá nhân, đổi mật khẩu

---

## 1. Sơ đồ tổng thể chức năng Staff

```mermaid
flowchart TD
    A["👨‍🍳 Staff đăng nhập"] --> B["/staff/orders"]
    
    B --> C["📊 Dashboard (/staff/dashboard)"]
    B --> D["🗺️ Sơ đồ bàn (/staff/tables)"]
    B --> E["📋 Quản lý Order (/staff/orders)"]
    B --> F["📅 Quản lý đặt bàn (/staff/reservations)"]
    B --> G["🧾 Lịch sử hóa đơn (/staff/invoices)"]
    B --> H["👤 Trang cá nhân (/staff/profile)"]
    
    D --> D1["Xem trạng thái bàn"]
    D --> D2["Mở bàn mới → Tạo Order"]
    
    E --> E1["Xem chi tiết Order"]
    E --> E2["Gọi thêm món"]
    E --> E3["Bắt đầu phục vụ"]
    E --> E4["Thanh toán"]
    E --> E5["Đóng bàn"]
    E --> E6["Hủy bàn"]
    
    F --> F1["Xác nhận đặt bàn"]
    F --> F2["Hủy đặt bàn"]
```

---

## 2. Luồng chính: Mở bàn → Gọi món → Thanh toán → Đóng bàn

Đây là luồng nghiệp vụ **quan trọng nhất** của Staff:

```mermaid
flowchart LR
    A["🪑 Bàn TRỐNG<br/>(AVAILABLE)"] -->|"Nhấn Mở Bàn"| B["📝 Tạo Order<br/>(Status: PENDING)"]
    B -->|"Bàn chuyển sang"| C["🔴 Bàn ĐANG DÙNG<br/>(OCCUPIED)"]
    
    C -->|"Nhấn Bắt đầu<br/>Phục vụ"| D["🍽️ Order: SERVING"]
    C -->|"Lỡ bấm nhầm?"| X["❌ Hủy Bàn<br/>→ Bàn về AVAILABLE"]
    
    D -->|"Gọi thêm món"| D
    D -->|"Thanh toán"| E["💰 Tạo Payment<br/>(Status: PAID)"]
    
    E -->|"Nhấn Đóng bàn"| F["✅ Order: COMPLETED<br/>Bàn: AVAILABLE"]
```

### Sequence Diagram chi tiết:

```mermaid
sequenceDiagram
    actor Staff as 👨‍🍳 Nhân viên
    participant Browser as 🌐 Trình duyệt
    participant OrderCtrl as 📋 StaffOrderController
    participant OrderSrv as ⚙️ OrderService
    participant ItemSrv as 🍕 OrderItemService
    participant PaySrv as 💳 PaymentService
    participant DB as 💾 Database

    Note over Staff,DB: === BƯỚC 1: MỞ BÀN ===

    Staff->>Browser: Ở sơ đồ bàn → Nhấn "Mở Bàn"
    Browser->>OrderCtrl: POST /staff/orders/create (tableId)
    OrderCtrl->>OrderSrv: createOrder(tableId, username)
    
    Note over OrderSrv: 1. Tạo Order mới (status=PENDING)<br/>2. Gán bàn + nhân viên<br/>3. Chuyển bàn → OCCUPIED
    
    OrderSrv->>DB: INSERT order + UPDATE table status
    OrderCtrl-->>Browser: redirect → /staff/orders/{id}

    Note over Staff,DB: === BƯỚC 2: GỌI MÓN ===

    Staff->>Browser: Chọn món từ menu → Bấm "Thêm"
    Browser->>OrderCtrl: POST /staff/orders/{id}/add-item (menuItemId, qty, note)
    OrderCtrl->>ItemSrv: addMenuItemToOrder(orderId, menuItemId, qty, note)
    
    Note over ItemSrv: 1. Lấy giá món hiện tại<br/>2. Tạo OrderItem (lưu unit_price)<br/>3. Cộng vào total_amount của Order
    
    ItemSrv->>DB: INSERT order_item + UPDATE order.total_amount
    OrderCtrl-->>Browser: redirect → /staff/orders/{id}

    Note over Staff,DB: === BƯỚC 3: BẮT ĐẦU PHỤC VỤ ===

    Staff->>Browser: Bấm "🍽️ Bắt đầu Phục vụ"
    Browser->>OrderCtrl: POST /staff/orders/{id}/update-status (status=SERVING)
    OrderCtrl->>OrderSrv: updateOrderStatus(id, SERVING)
    OrderSrv->>DB: UPDATE orders SET status='SERVING'
    OrderCtrl-->>Browser: redirect → /staff/orders/{id}

    Note over Staff,DB: === BƯỚC 4: THANH TOÁN ===

    Staff->>Browser: Chọn phương thức → Bấm "Thanh Toán"
    Browser->>OrderCtrl: POST /staff/orders/{id}/pay (paymentMethod=CASH)
    OrderCtrl->>PaySrv: createPayment(orderId, CASH)
    
    Note over PaySrv: 1. Tạo Payment (amount = order.total)<br/>2. Status = PAID
    
    PaySrv->>DB: INSERT payment
    OrderCtrl-->>Browser: redirect → /staff/orders/{id} + "Thanh toán thành công!"

    Note over Staff,DB: === BƯỚC 5: ĐÓNG BÀN ===

    Staff->>Browser: Bấm "Đóng Bàn"
    Browser->>OrderCtrl: POST /staff/orders/{id}/close-table
    OrderCtrl->>OrderSrv: checkout(orderId)
    
    Note over OrderSrv: 1. Order status → COMPLETED<br/>2. Bàn status → AVAILABLE<br/>3. Reservation (nếu có) → COMPLETED
    
    OrderSrv->>DB: UPDATE order + UPDATE table + UPDATE reservation
    OrderCtrl-->>Browser: redirect → /staff/orders + "Đã đóng bàn!"
```

---

## 3. Luồng Hủy bàn (khi bấm nhầm)

```mermaid
sequenceDiagram
    actor Staff as 👨‍🍳 Nhân viên
    participant Browser as 🌐 Trình duyệt
    participant Ctrl as 📋 StaffOrderController
    participant OrderSrv as ⚙️ OrderService
    participant DB as 💾 Database

    Staff->>Browser: Ở trang Order detail → Bấm "❌ Hủy Bàn"
    Browser->>Browser: Hiện confirm: "Bạn có chắc muốn hủy?"
    
    alt Bấm OK
        Browser->>Ctrl: POST /staff/orders/{id}/cancel
        Ctrl->>OrderSrv: cancelOrder(orderId)
        
        Note over OrderSrv: 1. Order status → CANCELLED<br/>2. Bàn → AVAILABLE<br/>3. Reservation (nếu có) → CANCELLED
        
        OrderSrv->>DB: UPDATE order + table + reservation
        Ctrl-->>Browser: redirect → /staff/orders + "Đã hủy bàn"
    else Bấm Cancel
        Browser-->>Staff: Không làm gì cả
    end
```

---

## 4. Sơ đồ trạng thái Order

```mermaid
stateDiagram-v2
    [*] --> PENDING: Mở bàn (Tạo Order)
    
    PENDING --> SERVING: Bấm "Bắt đầu Phục vụ"
    PENDING --> CANCELLED: Bấm "Hủy Bàn"
    
    SERVING --> SERVING: Gọi thêm món / Xóa món
    SERVING --> COMPLETED: Thanh toán + Đóng bàn
    
    COMPLETED --> [*]
    CANCELLED --> [*]
```

## 5. Sơ đồ trạng thái Bàn

```mermaid
stateDiagram-v2
    [*] --> AVAILABLE: Ban đầu

    AVAILABLE --> OCCUPIED: Mở bàn (tạo Order)
    AVAILABLE --> RESERVED: Khách đặt bàn online
    
    RESERVED --> OCCUPIED: Khách đến → Mở bàn
    RESERVED --> AVAILABLE: Hủy đặt bàn
    
    OCCUPIED --> AVAILABLE: Đóng bàn / Hủy bàn
    
    MAINTENANCE --> AVAILABLE: Sửa xong
    AVAILABLE --> MAINTENANCE: Bảo trì
```

---

## 6. Luồng quản lý đặt bàn (Staff)

```mermaid
sequenceDiagram
    actor Staff as 👨‍🍳 Nhân viên
    participant Browser as 🌐 Trình duyệt
    participant Ctrl as 📋 StaffReservationController
    participant ResSrv as 📅 ReservationService
    participant DB as 💾 Database

    Staff->>Browser: Vào "Quản lý đặt bàn"
    Browser->>Ctrl: GET /staff/reservations
    Ctrl->>ResSrv: findAll()
    Ctrl-->>Browser: Danh sách đặt bàn (filter theo trạng thái)

    alt Xác nhận đặt bàn
        Staff->>Browser: Bấm "✅ Xác nhận"
        Browser->>Ctrl: POST /staff/reservations/{id}/confirm
        Ctrl->>ResSrv: confirmReservation(id)
        ResSrv->>DB: UPDATE status='CONFIRMED', table='RESERVED'
        Ctrl-->>Browser: redirect + "Đã xác nhận"
    else Hủy đặt bàn
        Staff->>Browser: Bấm "❌ Hủy"
        Browser->>Ctrl: POST /staff/reservations/{id}/cancel
        Ctrl->>ResSrv: cancelReservation(id)
        ResSrv->>DB: UPDATE status='CANCELLED', table='AVAILABLE'
        Ctrl-->>Browser: redirect + "Đã hủy"
    end
```

---

## 7. Dashboard Staff

```mermaid
flowchart TD
    A["GET /staff/dashboard"] --> B["StaffDashboardController.dashboard()"]
    
    B --> C["📅 Đếm đặt bàn hôm nay"]
    B --> D["⏳ Đếm đặt bàn chờ xác nhận"]
    B --> E["🍽️ Đếm order đang xử lý"]
    B --> F["💰 Tính doanh thu hôm nay"]
    B --> G["🪑 Đếm bàn trống / bàn đang dùng"]
    B --> H["📋 Danh sách đặt bàn sắp tới"]
    
    C & D & E & F & G & H --> I["Trả staff/dashboard/index.html"]
```

---

## 8. Bảng tóm tắt các endpoint Staff

| HTTP | URL | Controller | Method | Mô tả |
|------|-----|-----------|--------|--------|
| GET | `/staff/dashboard` | StaffDashboardController | `dashboard()` | Tổng quan ca làm |
| GET | `/staff/tables` | StaffTableController | `index()` | Sơ đồ bàn |
| GET | `/staff/orders` | StaffOrderController | `index()` | DS order + sơ đồ bàn |
| POST | `/staff/orders/create` | StaffOrderController | `createOrder()` | Mở bàn mới |
| GET | `/staff/orders/{id}` | StaffOrderController | `orderDetail()` | Chi tiết Order |
| POST | `/staff/orders/{id}/add-item` | StaffOrderController | `addItem()` | Thêm món |
| GET | `/staff/orders/{id}/delete-item/{itemId}` | StaffOrderController | `deleteItem()` | Xóa món |
| POST | `/staff/orders/{id}/update-status` | StaffOrderController | `updateStatus()` | Đổi trạng thái |
| POST | `/staff/orders/{id}/pay` | StaffOrderController | `payOrder()` | Thanh toán |
| POST | `/staff/orders/{id}/close-table` | StaffOrderController | `closeTable()` | Đóng bàn |
| POST | `/staff/orders/{id}/cancel` | StaffOrderController | `cancelOrder()` | Hủy bàn |
| GET | `/staff/reservations` | StaffReservationController | `index()` | DS đặt bàn |
| POST | `/staff/reservations/{id}/confirm` | StaffReservationController | `confirm()` | Xác nhận |
| POST | `/staff/reservations/{id}/cancel` | StaffReservationController | `cancel()` | Hủy đặt bàn |
| GET | `/staff/invoices` | StaffInvoiceController | `index()` | Lịch sử hóa đơn |
| GET | `/staff/invoices/{id}` | StaffInvoiceController | `detail()` | Chi tiết hóa đơn |
| GET | `/staff/profile` | StaffProfileController | `index()` | Thông tin cá nhân |
| POST | `/staff/profile/update` | StaffProfileController | `update()` | Cập nhật thông tin |
| POST | `/staff/profile/change-password` | StaffProfileController | `changePassword()` | Đổi mật khẩu |
