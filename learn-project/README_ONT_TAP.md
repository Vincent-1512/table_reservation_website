# 🎓 RESTAURANT PROJECT - PREPARATION SUMMARY

---

## ✅ TÀI LIỆU CHUẨN BỊ VĂN ĐÁP

Tôi đã tạo ra **4 file tài liệu** chi tiết để bạn ôn tập:

### 📄 **1. PHAN_TICH_DU_AN_CHI_TIET.md** (CHỈ TÀU)
- ✍️ **100+ KB**, full analysis
- 📌 Giới thiệu project, công nghệ, dependencies
- 🏗️ Cấu trúc thư mục chi tiết
- 🔄 Luồng hoạt động (startup, auth, login, đặt bàn)
- 📊 Database schema & entity relationships
- 🔐 Spring Security configuration
- 💡 **10 Câu hỏi vấn đáp phổ biến + Trả lời đầy đủ**
- ✓ Nên **in ra 30 trang** để đọc trước khi thi

---

### 📖 **2. HUONG_DAN_CHAY_DU_AN.md** (PRACTICAL GUIDE)
- 🚀 Hướng dẫn chạy project (Docker + Local)
- 🛠️ Troubleshooting lỗi phổ biến
- 🔧 Advanced Docker commands
- 💡 Tips & Tricks (Hot reload, Debug, Profiles)
- ✓ **Thực hiện lần lượt từng step để verify**

---

### 📌 **3. CHEAT_SHEET.md** (1 TRANG)
- ⚡ Tómo tắt toàn bộ project (1 trang)
- 📊 Bảng tómo tắt entities, endpoints, roles
- ❓ 10 câu hỏi + trả lời ngắn gọn
- ✓ **In ra, mang theo khi thi** (nhìn lại nhanh)

---

### 💾 **4. DATABASE_SCHEMA.md** (DATABASE DEEP DIVE)
- 📊 ER Diagram chi tiết (ASCII art)
- 🔑 8 tables + SQL schema
- 🔍 Relationships (1:n, n:1, 1:1)
- 💪 Enums (Role, OrderStatus, TableStatus...)
- 🔒 Optimistic Locking explanation
- 📈 Query examples
- ✓ **Hiểu database = Hiểu project 70%**

---

## 🎯 ROADMAP ÔN TẬP (3-5 NGÀY)

### **Ngày 1: Build & Run**
```
1. Đọc HUONG_DAN_CHAY_DU_AN.md
2. Thực hiện: docker-compose up --build
3. Login với account test (admin/admin123)
4. Thử các tính năng:
   ✓ Xem menu
   ✓ Thêm giỏ hàng
   ✓ Đặt bàn
   ✓ Admin quản lý menu
   ✓ Admin quản lý bàn
   ✓ Staff quản lý đơn hàng
```

### **Ngày 2-3: Hiểu Kiến Trúc**
```
1. Đọc PHAN_TICH_DU_AN_CHI_TIET.md (Part I-V)
   - Công nghệ & dependencies
   - Cấu trúc folder
   - Luồng khởi động
   - Auth flow

2. Đọc DATABASE_SCHEMA.md (Full)
   - Hiểu 8 entities
   - Entity relationships
   - Optimistic Locking
   - Query examples

3. Đọc PHAN_TICH_DU_AN_CHI_TIET.md (Part VI-X)
   - Spring Security config
   - CartService
   - Key files
   - 10 Câu hỏi
```

### **Ngày 4: Code Deep Dive**
```
1. Mở Eclipse/IDE
2. Đọc lần lượt các file quan trọng:
   - RestaurantApplication.java
   - SecurityConfig.java
   - AuthController.java
   - CustomerController.java
   - AdminMenuController.java
   - CartService.java
   - User.java, Order.java, MenuItem.java
   - UserService.java, OrderService.java

3. Trace flow:
   - Khi user login → where does data go?
   - Khi user đặt bàn → what tables are affected?
   - Khi staff confirm order → how status changes?

4. Thử modify code:
   - Thêm 1 field vào MenuItem (ví dụ: origin)
   - Cập nhật form HTML
   - Chạy lại xem có lỗi không
```

### **Ngày 5: Mock Interview**
```
1. Đọc lại CHEAT_SHEET.md
2. Tự hỏi 10 câu hỏi từ PHAN_TICH_DU_AN_CHI_TIET.md
3. Trả lời mà không nhìn tài liệu
4. Kiểm tra câu trả lời có đúng không
5. Nếu sai → Đọc lại phần liên quan
6. Lặp lại cho đến khi trả lời ok all
```

---

## 🎓 TOP 10 CÂU HỎI SẼ BỊ HỎI

### **Q1: Giới thiệu dự án của bạn?**
```
Là một ứng dụng web Spring Boot quản lý đặt bàn và menu điện tử.
Có 3 vai trò: ADMIN (quản lý hệ thống), STAFF (nhân viên), CUSTOMER (khách).
Dùng MySQL để lưu data, Thymeleaf để render view, Spring Security để bảo mật.
```

### **Q2: Tại sao dùng Spring Boot thay vì JSF/Struts?**
```
- Spring Boot: Không cần config phức tạp (starter dependencies)
- Tích hợp sẵn Tomcat → không cần deploy WAR
- Spring Security build-in
- Spring Data JPA: Query dễ, ít boilerplate code
- Community lớn, tài liệu nhiều
```

### **Q3: Database có mấy bảng? Relationship?**
```
8 bảng: users, categories, menu_items, areas, tables, reservations, orders, order_items

Main relationships:
- users (1:n) reservations → customers make reservations
- users (1:n) orders → staff create orders
- tables (1:n) reservations → tables can be reserved multiple times
- tables (1:n) orders → tables have multiple orders
- orders (1:n) order_items → orders have multiple items
- categories (1:n) menu_items → categories have multiple items
```

### **Q4: Spring Security hoạt động sao?**
```
1. User submit login form (POST /login)
2. SecurityFilterChain intercept request
3. CustomUserDetailsService.loadUserByUsername()
   - Query UserRepository.findByUsername()
   - Return UserDetails (với password hash)
4. BCryptPasswordEncoder so sánh password input vs hash
5. Nếu match → tạo session + call CustomAuthenticationSuccessHandler
6. Handler check role → redirect:
   - ADMIN → /admin/menu
   - STAFF → /staff/orders
   - CUSTOMER → /
7. Spring Security set SecurityContext với auth info
8. Lần request tiếp theo: Spring kiểm tra session → allow/deny dựa vào role
```

### **Q5: Khi 2 customer cùng đặt 1 bàn, sao không bị conflict?**
```
Dùng Optimistic Locking (@Version):

1. Table load từ DB với version = 0
2. Customer A update table → version = 1 ✓
3. Customer B thực hiện update table (version = 0 vs 1)
   → Hibernate throw ObjectOptimisticLockingFailureException
4. Controller catch → hiển thị lỗi: "Bàn vừa bị đặt, chọn bàn khác"

Advantage:
- Không cần lock DB
- Performance tốt
- Tối ưu cho concurrent access
```

### **Q6: CartService dùng @SessionScope? Tại sao?**
```
@SessionScope = mỗi HTTP session có 1 instance CartService

Vì sao:
- Giỏ hàng là personal (mỗi user khác nhau)
- Session tự động xóa khi timeout/logout
- Không cần lưu DB → performance tốt
- Không cần query lại mỗi lần

How it works:
1. User add item → CartService.add() → lưu vào Map (memory)
2. User view cart → CartService.getItems() → hiển thị từ Map
3. User logout → session destroyed → CartService.clear()
4. User login lại → CartService mới được tạo (empty)
```

### **Q7: ADMIN có quyền gì? STAFF? CUSTOMER?**
```
ADMIN:
- /admin/menu (CRUD menu, categories)
- /admin/table (CRUD tables, areas)
- /admin/report (view doanh thu, export Excel/PDF)

STAFF:
- /staff/orders (xem tất cả orders)
- /staff/reservation (xem & confirm reservations)
- Xác nhận/hoàn thành order
- Cập nhật table status

CUSTOMER:
- /menu (xem menu)
- /cart (quản lý giỏ hàng)
- /reservation (đặt bàn)
- /my-reservations (xem lịch sử đặt)
```

### **Q8: Password được lưu sao? An toàn không?**
```
Dùng BCryptPasswordEncoder:

Flow:
1. User input: "password123"
2. BCrypt mã hóa → "$2a$10$Xm7eUkmzI6VVwxEQfL2UbeC/Mh0sVHXYLY6rFSGbg.nHqEKzP..." (60 ký tự)
3. Lưu hash vào DB (NOT original password)
4. Khi login, BCrypt so sánh input vs hash
5. Nếu match → OK, không match → Fail

An toàn:
✓ Không thể reverse (one-way hash)
✓ Mỗi hash khác nhau (salt random)
✓ Không lưu original password
✓ Même nếu hacker lấy được DB → không thể đoán password
```

### **Q9: Nếu muốn thêm role mới (MANAGER)?**
```
3 bước:

1. Thêm vào enum Role:
   public enum Role {
       ADMIN, STAFF, CUSTOMER, MANAGER
   }

2. Cập nhật SecurityConfig:
   .requestMatchers("/manager/**").hasRole("MANAGER")

3. Tạo ManagerController.java:
   @Controller
   @RequestMapping("/manager")
   @PreAuthorize("hasRole('MANAGER')")
   public class ManagerController { ... }

4. Sau deploy:
   UPDATE users SET role='MANAGER' WHERE id=5;

5. User login → Spring tự gán ROLE_MANAGER
```

### **Q10: Xuất báo cáo Excel/PDF sao?**
```
Excel (Apache POI):
1. ReportService.generateReport()
   - Query database
   - Tính tổng doanh thu, số order...
   - Tạo ReportDTO object
2. ExcelExportService.export()
   - new XSSFWorkbook() tạo file
   - Thêm sheet, rows, cells
   - Set format (bold, color...)
   - Write to OutputStream
3. Controller set response header
   - Content-Type: application/vnd.openxmlformats...
   - Content-Disposition: attachment; filename=report.xlsx
4. Browser download file

PDF (OpenPDF):
- Tương tự, nhưng dùng PdfExportService
- Create document → add paragraphs/tables → write to OutputStream
```

---

## 🔧 TECHNICAL DETAILS TO MENTION

1. **JPA vs Hibernate**
   - JPA: Specification (interface)
   - Hibernate: Implementation
   - Repository: Extends JpaRepository → auto CRUD methods

2. **@ManyToOne vs @OneToMany**
   - @ManyToOne: Trong entity có FK
   - @OneToMany: Inverse side, không có FK
   - @JoinColumn: Specify FK name

3. **Lazy vs Eager Loading**
   - Lazy (default): Load khi access
   - Eager: Load ngay lập tức
   - Cart dùng Lazy → tránh N+1 query problem

4. **Session Scope lifetime**
   - Tạo: user first request
   - Destroy: timeout (default 30 min) hoặc logout
   - Mỗi browser tab = 1 session

5. **CSRF Protection**
   - Enabled by default (Spring Security 6+)
   - Form phải có `_csrf` token
   - POST/PUT/DELETE bị protect tự động

---

## 📚 FILE MAPPING (Nếu được hỏi)

```
┌─ controller/
│  ├─ AuthController.java              → Login/Register
│  ├─ HomeController.java              → Trang chủ
│  ├─ CustomerController.java          → Menu, Cart, Reservation
│  ├─ AdminMenuController.java         → Quản lý Menu
│  ├─ AdminTableController.java        → Quản lý Bàn
│  ├─ AdminReportController.java       → Báo cáo & Export
│  └─ StaffOrderController.java        → Quản lý Đơn Hàng
│
├─ service/
│  ├─ CartService.java                 → Session cart
│  ├─ UserService.java                 → User logic
│  ├─ MenuItemService.java             → Menu items
│  ├─ OrderService.java                → Orders
│  ├─ ReservationService.java          → Reservations
│  ├─ ExcelExportService.java          → Export Excel
│  └─ impl/                            → Implementations
│
├─ repository/
│  ├─ UserRepository.java              → User queries
│  ├─ MenuItemRepository.java          → MenuItem queries
│  ├─ OrderRepository.java             → Order queries
│  └─ ...
│
├─ entity/
│  ├─ User.java                        → User model
│  ├─ Order.java                       → Order model
│  ├─ MenuItem.java                    → MenuItem model
│  ├─ Reservation.java                 → Reservation model
│  ├─ DiningTable.java                 → Table model
│  └─ enums/                           → Enum classes
│
├─ security/
│  ├─ SecurityConfig.java              → Security rules
│  ├─ CustomUserDetailsService.java    → Load user
│  └─ CustomAuthenticationSuccessHandler.java → Redirect logic
│
└─ resources/
   ├─ application.properties           → Local config
   ├─ application-docker.properties    → Docker config
   └─ templates/                       → HTML views
      ├─ index.html
      ├─ login.html
      ├─ admin/
      ├─ customer/
      └─ staff/
```

---

## 💡 PHẦN TRỊ CÓ THỂ HỎIHÔM

1. **Mô tả flow khi khách đặt bàn từ đầu đến cuối**
2. **So sánh Spring Security vs simple session management**
3. **Tại sao dùng CartService session mà không dùng database?**
4. **Làm sao scale ứng dụng (thêm user nhiều)?**
5. **Cách xử lý transaction khi customer vừa thanh toán, server bị crash?**
6. **Performance optimization: N+1 query problem?**
7. **Security issue: SQL injection, CSRF, XSS?**
8. **Logging & debugging strategy?**
9. **Unit test & Integration test?**
10. **CI/CD pipeline?**

---

## ✨ FINAL TIPS

1. **Chuẩn bị tómo tắt 1 trang** (nên in CHEAT_SHEET.md)
2. **Đọc code trong IDE** (Eclipse) → hiểu flow tốt hơn
3. **Chạy trực tiếp** (docker-compose up) → thấy real behavior
4. **Vẽ diagram** (Entity Relationship, Request Flow) → giải thích tốt
5. **Trả lời chi tiết** → không chỉ "vâng thầy", mà nói rõ "vì sao"
6. **Chuẩn bị câu hỏi ngược** → nếu thầy hỏi gì bạn không biết
7. **Tự tin** → bạn đã chuẩn bị kỹ càng!

---

## 📞 QUICK REFERENCE

### Build & Run
```bash
docker-compose up --build
# or
./mvnw spring-boot:run
```

### Default Account
```
admin / admin123
staff / staff123
```

### Key Packages
```
vn.edu.ptit.restaurant.{
  controller,
  service,
  repository,
  entity,
  security,
  dto
}
```

### Main Classes
```
- RestaurantApplication.java → @SpringBootApplication
- SecurityConfig.java → Spring Security
- CartService.java → @SessionScope
- User, Order, MenuItem, Reservation, DiningTable
```

---

**🎊 CHÚC BẠN THI TỐT! 🎉**

**Bạn đã chuẩn bị kỹ càng, tự tin lên đó! 💪**

---

*Tài liệu chuẩn bị bởi: GitHub Copilot*

*Thời gian: Tháng 5 năm 2026*

*Địa điểm: Workspace PTIT*
