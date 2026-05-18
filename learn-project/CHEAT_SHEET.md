# 📌 CHEAT SHEET RESTAURANT PROJECT (1 trang)

---

## 🎯 TÓMO TẮT PROJECT

| Aspect | Chi tiết |
|--------|---------|
| **Tên** | Restaurant - Web Đặt Bàn & Menu Điện Tử |
| **Framework** | Spring Boot 4.0.6 + Thymeleaf |
| **Database** | MySQL 8.0 (Hibernate ORM) |
| **Security** | Spring Security + BCrypt |
| **Java** | 17+ |
| **Port** | 8080 |
| **Roles** | ADMIN, STAFF, CUSTOMER |

---

## 🏗️ CẤU TRÚC THƠMC

```
controller/        ← Xử lý HTTP request
service/           ← Business logic
repository/        ← Truy vấn DB (JPA)
entity/            ← Models (User, Order, MenuItem...)
security/          ← Auth + Authorization
dto/               ← Data Transfer Objects
```

---

## 📊 8 ENTITIES & QUAN HỆ

```
[users] (1:n) ─────► [reservations] (1:n) ─────► [orders] (1:n) ─────► [order_items]
                                                         │
                                                         └─► [tables] (n:1) ─► [areas]

[menu_items] (n:1) ─► [categories]

Mỗi ORDER có nhiều ORDER_ITEMS (chi tiết từng món)
Mỗi USER có nhiều RESERVATIONS
Mỗi TABLE có nhiều RESERVATIONS
Mỗi AREA có nhiều TABLES
Mỗi CATEGORY có nhiều MENU_ITEMS
```

---

## 🔐 SPRING SECURITY RULES

```
Public Endpoints:
  "/" → Tất cả
  "/login", "/register" → Tất cả
  "/css/**", "/js/**", "/images/**" → Tất cả

Protected Endpoints:
  "/admin/**" → ROLE_ADMIN chỉ
  "/staff/**" → ROLE_ADMIN, ROLE_STAFF
  "/customer/**" → ROLE_ADMIN, ROLE_STAFF, ROLE_CUSTOMER
  Các endpoint khác → Phải authenticated
```

---

## 🔄 LOGIN FLOW

```
1. POST /login (username + password)
   ↓
2. CustomUserDetailsService.loadUserByUsername() → lấy từ DB
   ↓
3. BCryptPasswordEncoder so sánh password
   ↓
4. Nếu OK → CustomAuthenticationSuccessHandler
   ↓
5. Kiểm tra role → redirect:
   - ADMIN → /admin/menu
   - STAFF → /staff/orders
   - CUSTOMER → /
```

---

## 🛒 CARTSERVICE (@SessionScope)

```
- Lưu giỏ hàng trong HTTP Session (memory)
- Mỗi user có 1 instance CartService riêng
- Khi logout → session xóa → cart xóa
- Methods: add(), remove(), update(), clear(), getAmount()
```

---

## 💾 KEY FILES

| File | Tác dụng |
|------|---------|
| RestaurantApplication.java | Entry point |
| SecurityConfig.java | Spring Security config |
| CartService.java | Quản lý giỏ hàng |
| CustomUserDetailsService.java | Load user từ DB |
| Customer/AdminController.java | Handle request |
| UserService/OrderService/... | Business logic |
| Entity/*.java | Models (8 cái) |
| templates/*.html | Thymeleaf views |
| application.properties | Config DB/port |
| pom.xml | Dependencies |
| docker-compose.yml | Docker config |

---

## 🚀 CHẠY DỰ ÁN

### Option 1: Docker (1 lệnh)
```bash
docker-compose up --build
# Open: http://localhost:8080
```

### Option 2: Local
```bash
# Sửa application.properties (DB username/password)
./mvnw clean package -DskipTests
java -jar target/restaurant-0.0.1-SNAPSHOT.jar
# Open: http://localhost:8080
```

---

## 🔐 DEFAULT ACCOUNTS

| Role | Username | Password |
|------|----------|----------|
| ADMIN | admin | admin123 |
| STAFF | staff | staff123 |

---

## ❓ 10 CÂU HỎI THƯỜNG GẶP

### Q1: Có mấy vai trò?
**A:** 3 vai trò: ADMIN (quản lý hệ thống), STAFF (nhân viên), CUSTOMER (khách hàng)

### Q2: Khi 2 người đặt bàn giống nhau?
**A:** Dùng @Version (Optimistic Locking) → người thứ 2 catch ObjectOptimisticLockingFailureException

### Q3: Cart được lưu ở đâu?
**A:** Trong HTTP Session (memory), xóa khi logout

### Q4: Password mã hóa như nào?
**A:** BCryptPasswordEncoder (không thể giải mã, chỉ so sánh)

### Q5: Thêm role mới (MANAGER)?
**A:** Thêm vào enum Role → sửa SecurityConfig → restart

### Q6: Nếu server restart, cart mất không?
**A:** Có, session mất. Nếu muốn giữ → lưu vào DB/Redis

### Q7: Có API REST không?
**A:** Không, chỉ có Web MVC (trả HTML via Thymeleaf)

### Q8: Làm sao upload hình cho menu?
**A:** Thêm file upload field → save filename → hiển thị via Thymeleaf

### Q9: Xuất Excel/PDF?
**A:** ReportService → ExcelExportService/PdfExportService (Apache POI/OpenPDF)

### Q10: Database error thì sao?
**A:** Controller catch → set error message → template hiển thị

---

## 🗂️ DEPENDENCIES

```xml
- Spring Boot Starter Web (MVC)
- Spring Boot Starter Data JPA (ORM)
- Spring Boot Starter Security (Auth)
- Spring Boot Starter Thymeleaf (View)
- MySQL Connector (JDBC)
- Lombok (Boilerplate)
- Apache POI (Excel)
- OpenPDF (PDF)
- Hibernate 7.2.12 (ORM)
```

---

## 🎯 MAIN CONCEPTS

1. **MVC Pattern**: Model (Entity/Service) + View (Thymeleaf) + Controller
2. **JPA/Hibernate**: Map Entity → SQL table, Repository → JPA query
3. **Spring Security**: Authentication (login) + Authorization (roles)
4. **Session Scope**: CartService lưu trong session
5. **Optimistic Locking**: @Version cho concurrent updates
6. **Form Login**: CustomAuthenticationSuccessHandler redirect theo role
7. **BCrypt**: Hash password, không reversible

---

## ✅ CHECKLIST QUICK

- [ ] Nắm 3 vai trò + quyền
- [ ] Hiểu login flow + Spring Security
- [ ] Biết 8 entities + relationships
- [ ] Hiểu CartService (@SessionScope)
- [ ] Biết đặt bàn + race condition handling
- [ ] Có thể chạy project (Docker/Local)
- [ ] Biết trả lời 10 câu hỏi phổ biến
- [ ] Đọc xong file PHAN_TICH_DU_AN_CHI_TIET.md

---

## 🔗 QUICK ENDPOINTS

```
Public:
  GET /                 → Trang chủ
  GET /login            → Form login
  GET /register         → Form register
  POST /register        → Đăng ký

Customer:
  GET /menu             → Xem menu
  POST /cart/add        → Thêm vào giỏ
  GET /cart             → Xem giỏ hàng
  GET /reservation      → Form đặt bàn
  POST /reservation     → Submit đặt bàn
  GET /my-reservations  → Xem lịch sử đặt

Admin:
  GET /admin/menu       → Quản lý menu
  POST /admin/menu/*/save    → Lưu
  GET /admin/table      → Quản lý bàn
  GET /admin/report     → Báo cáo

Staff:
  GET /staff/orders     → Xem đơn hàng
  POST /staff/order/*/confirm    → Xác nhận

All:
  GET /logout           → Đăng xuất
```

---

**Print & Mang theo khi thi! 📄**
