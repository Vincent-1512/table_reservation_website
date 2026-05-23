# 🛡️ LUỒNG NGHIỆP VỤ ADMIN (Quản trị viên)

## Tổng quan chức năng

Admin sau khi đăng nhập được chuyển tới `/admin/reports` và có quyền truy cập **toàn bộ** chức năng hệ thống:
1. **Báo cáo & Thống kê** → Doanh thu, số liệu, xuất Excel/PDF
2. **Quản lý thực đơn** → CRUD Danh mục + Món ăn
3. **Quản lý bàn & khu vực** → CRUD Bàn ăn + Khu vực
4. **Quản lý đơn hàng** → Xem, lọc, phân trang
5. **Quản lý đặt bàn** → Xem, lọc theo trạng thái, xác nhận / hủy
6. **Quản lý thanh toán** → Xem danh sách, xử lý hoàn tiền
7. **Quản lý người dùng** → Xem, lọc, phân trang, xóa mềm

> 💡 Admin cũng có thể truy cập toàn bộ trang `/staff/**` nhờ cấu hình `hasAnyRole("ADMIN", "STAFF")`.

---

## 1. Sơ đồ tổng thể chức năng Admin

```mermaid
flowchart TD
    A["🛡️ Admin đăng nhập"] --> B["/admin/reports"]
    
    B --> C["📊 Báo cáo (/admin/reports)"]
    B --> D["🍕 Quản lý Thực đơn (/admin/menu)"]
    B --> E["🪑 Quản lý Bàn (/admin/tables)"]
    B --> F["📋 Quản lý Đơn hàng (/admin/orders)"]
    B --> G["📅 Quản lý Đặt bàn (/admin/reservations)"]
    B --> H["💳 Quản lý Thanh toán (/admin/payments)"]
    B --> I["👥 Quản lý Người dùng (/admin/users)"]
    
    C --> C1["Xem tổng quan doanh thu"]
    C --> C2["📥 Xuất Excel (.xlsx)"]
    C --> C3["📥 Xuất PDF"]
    
    D --> D1["Thêm / Sửa / Xóa danh mục"]
    D --> D2["Thêm / Sửa / Xóa món ăn"]
    
    E --> E1["Thêm / Sửa / Xóa khu vực"]
    E --> E2["Thêm / Sửa / Xóa bàn ăn"]
    
    F --> F1["Xem chi tiết đơn hàng"]
    F --> F2["Lọc theo trạng thái / ngày"]
    
    G --> G1["Xác nhận đặt bàn"]
    G --> G2["Hủy đặt bàn"]
    
    H --> H1["Xem danh sách thanh toán"]
    H --> H2["Hoàn tiền"]
    
    I --> I1["Lọc theo role / keyword"]
    I --> I2["Xóa mềm người dùng"]
```

---

## 2. Luồng Quản lý Thực đơn (Menu)

```mermaid
sequenceDiagram
    actor Admin as 🛡️ Admin
    participant Browser as 🌐 Trình duyệt
    participant Ctrl as 📋 AdminMenuController
    participant CatSrv as 📂 CategoryService
    participant MenuSrv as 🍕 MenuItemService
    participant DB as 💾 Database

    Admin->>Browser: Vào "Quản lý Thực đơn"
    Browser->>Ctrl: GET /admin/menu
    Ctrl->>CatSrv: findAll()
    Ctrl->>MenuSrv: findAll() hoặc findPaginated()
    Ctrl-->>Browser: Trả trang admin/menu/index.html

    Note over Admin,DB: === THÊM MÓN MỚI ===

    Admin->>Browser: Điền thông tin món → Bấm "Thêm"
    Browser->>Ctrl: POST /admin/menu/items/create (name, price, categoryId, imageUrl)
    Ctrl->>MenuSrv: save(menuItem)
    MenuSrv->>DB: INSERT INTO menu_items (...)
    Ctrl-->>Browser: redirect → /admin/menu

    Note over Admin,DB: === SỬA MÓN ===

    Admin->>Browser: Bấm "Sửa" trên 1 món
    Browser->>Ctrl: GET /admin/menu/items/{id}/edit
    Ctrl-->>Browser: Trả form sửa + dữ liệu hiện tại

    Admin->>Browser: Sửa thông tin → Bấm "Lưu"
    Browser->>Ctrl: POST /admin/menu/items/{id}/update (data)
    Ctrl->>MenuSrv: save(menuItem)
    MenuSrv->>DB: UPDATE menu_items SET ...
    Ctrl-->>Browser: redirect → /admin/menu

    Note over Admin,DB: === XÓA MÓN ===

    Admin->>Browser: Bấm "Xóa"
    Browser->>Ctrl: POST /admin/menu/items/{id}/delete
    Ctrl->>MenuSrv: delete(id)
    MenuSrv->>DB: DELETE FROM menu_items WHERE id = ?
    Ctrl-->>Browser: redirect → /admin/menu
```

---

## 3. Luồng Quản lý Bàn & Khu vực

```mermaid
sequenceDiagram
    actor Admin as 🛡️ Admin
    participant Browser as 🌐 Trình duyệt
    participant Ctrl as 📋 AdminTableController
    participant AreaSrv as 🏢 AreaService
    participant TableSrv as 🪑 DiningTableService
    participant DB as 💾 Database

    Admin->>Browser: Vào "Quản lý Bàn"
    Browser->>Ctrl: GET /admin/tables
    Ctrl->>AreaSrv: findAll() → DS khu vực
    Ctrl->>TableSrv: findAll() → DS bàn (có thể lọc theo area/status)
    Ctrl-->>Browser: Trả admin/table/index.html

    Note over Admin,DB: === THÊM KHU VỰC ===
    Admin->>Browser: Nhập tên khu vực → Bấm "Thêm"
    Browser->>Ctrl: POST /admin/tables/areas/create (name, description)
    Ctrl->>AreaSrv: save(area)
    AreaSrv->>DB: INSERT INTO areas (...)
    Ctrl-->>Browser: redirect → /admin/tables

    Note over Admin,DB: === THÊM BÀN ===
    Admin->>Browser: Nhập tên bàn, sức chứa, khu vực → Bấm "Thêm"
    Browser->>Ctrl: POST /admin/tables/create (tableName, capacity, areaId)
    Ctrl->>TableSrv: save(table)
    TableSrv->>DB: INSERT INTO tables (...)
    Ctrl-->>Browser: redirect → /admin/tables
```

---

## 4. Luồng Báo cáo & Xuất file

```mermaid
sequenceDiagram
    actor Admin as 🛡️ Admin
    participant Browser as 🌐 Trình duyệt
    participant Ctrl as 📋 AdminReportController
    participant RptSrv as 📊 ReportService
    participant ExcelSrv as 📗 ExcelExportService
    participant PdfSrv as 📕 PdfExportService
    participant DB as 💾 Database

    Admin->>Browser: Vào "Báo cáo"
    Browser->>Ctrl: GET /admin/reports
    Ctrl->>RptSrv: getGeneralReport() + countOrders() + countTables() ...
    RptSrv->>DB: SELECT COUNT, SUM queries
    Ctrl-->>Browser: Trả admin/report/dashboard.html

    Note over Admin,DB: === XUẤT EXCEL ===
    Admin->>Browser: Bấm "Xuất Excel"
    Browser->>Ctrl: GET /admin/reports/export/excel
    Ctrl->>ExcelSrv: exportRevenueToExcel()
    Note over ExcelSrv: Dùng Apache POI<br/>Tạo file .xlsx trong memory
    ExcelSrv->>DB: SELECT orders + payments data
    ExcelSrv-->>Ctrl: ByteArrayInputStream
    Ctrl-->>Browser: Response 200 + file download (revenue_report.xlsx)

    Note over Admin,DB: === XUẤT PDF ===
    Admin->>Browser: Bấm "Xuất PDF"
    Browser->>Ctrl: GET /admin/reports/export/pdf
    Ctrl->>PdfSrv: exportReservationsToPdf()
    Note over PdfSrv: Dùng OpenPDF<br/>Tạo file .pdf trong memory
    PdfSrv->>DB: SELECT reservations data
    PdfSrv-->>Ctrl: ByteArrayInputStream
    Ctrl-->>Browser: Response 200 + file download (reservation_report.pdf)
```

---

## 5. Luồng Quản lý Người dùng

```mermaid
sequenceDiagram
    actor Admin as 🛡️ Admin
    participant Browser as 🌐 Trình duyệt
    participant Ctrl as 📋 AdminUserController
    participant UserSrv as 👥 UserService
    participant DB as 💾 Database

    Admin->>Browser: Vào "Quản lý Người dùng"
    Browser->>Ctrl: GET /admin/users (page=0, size=2, role=?, keyword=?)
    Ctrl->>UserSrv: findPaginated() hoặc filterUsers()
    UserSrv->>DB: SELECT * FROM users (có phân trang)
    Ctrl-->>Browser: Trả admin/user/index.html + phân trang

    Note over Admin,DB: === XÓA MỀM NGƯỜI DÙNG ===
    Admin->>Browser: Bấm "Xóa" trên 1 user
    Browser->>Ctrl: POST /admin/users/{id}/delete
    Ctrl->>UserSrv: softDelete(id)
    Note over UserSrv: Không xóa thật<br/>Chỉ SET deleted = true
    UserSrv->>DB: UPDATE users SET deleted = 1
    Ctrl-->>Browser: redirect → /admin/users + "Đã xóa"
```

> 💡 **Xóa mềm (Soft Delete)**: Người dùng không bị xóa khỏi DB mà chỉ bị đánh dấu `deleted = true`. Dữ liệu liên quan (orders, reservations) vẫn được giữ nguyên.

---

## 6. Bảng tóm tắt các endpoint Admin

| HTTP | URL | Controller | Mô tả |
|------|-----|-----------|--------|
| GET | `/admin/reports` | AdminReportController | Dashboard báo cáo |
| GET | `/admin/reports/export/excel` | AdminReportController | Xuất file Excel |
| GET | `/admin/reports/export/pdf` | AdminReportController | Xuất file PDF |
| GET | `/admin/menu` | AdminMenuController | DS danh mục + món ăn |
| POST | `/admin/menu/categories/create` | AdminMenuController | Thêm danh mục |
| POST | `/admin/menu/categories/{id}/update` | AdminMenuController | Sửa danh mục |
| POST | `/admin/menu/categories/{id}/delete` | AdminMenuController | Xóa danh mục |
| POST | `/admin/menu/items/create` | AdminMenuController | Thêm món |
| POST | `/admin/menu/items/{id}/update` | AdminMenuController | Sửa món |
| POST | `/admin/menu/items/{id}/delete` | AdminMenuController | Xóa món |
| GET | `/admin/tables` | AdminTableController | DS khu vực + bàn |
| POST | `/admin/tables/areas/create` | AdminTableController | Thêm khu vực |
| POST | `/admin/tables/create` | AdminTableController | Thêm bàn |
| POST | `/admin/tables/{id}/update` | AdminTableController | Sửa bàn |
| POST | `/admin/tables/{id}/delete` | AdminTableController | Xóa bàn |
| GET | `/admin/orders` | AdminOrderController | DS đơn hàng (lọc, phân trang) |
| GET | `/admin/orders/{id}` | AdminOrderController | Chi tiết đơn hàng |
| GET | `/admin/reservations` | AdminReservationController | DS đặt bàn |
| POST | `/admin/reservations/{id}/confirm` | AdminReservationController | Xác nhận |
| POST | `/admin/reservations/{id}/cancel` | AdminReservationController | Hủy |
| GET | `/admin/payments` | AdminPaymentController | DS thanh toán |
| POST | `/admin/payments/{id}/refund` | AdminPaymentController | Hoàn tiền |
| GET | `/admin/users` | AdminUserController | DS người dùng |
| POST | `/admin/users/{id}/delete` | AdminUserController | Xóa mềm |
