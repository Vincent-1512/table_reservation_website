# 📋 PHÂN TÍCH LUỒNG DỰ ÁN RESTAURANT - HƯỚNG DẪN ÔN VĂN ĐÁP

---

## 📌 I. GIỚI THIỆU DỰ ÁN

### Tên dự án: **Restaurant - Web Đặt Bàn và Menu Điện Tử**

### Mô tả:
- Đây là một **ứng dụng web Spring Boot** giúp khách hàng đặt bàn ăn tại nhà hàng và xem menu điện tử
- Hệ thống có 3 vai trò chính: **ADMIN**, **STAFF**, **CUSTOMER**
- Cho phép quản lý bàn ăn, menu, danh mục, đơn đặt bàn, đơn hàng và tạo báo cáo

### Công nghệ sử dụng:
| Công nghệ | Phiên bản | Mục đích |
|-----------|----------|---------|
| **Spring Boot** | 4.0.6 | Framework web |
| **Spring Security** | 6.0+ | Xác thực & phân quyền |
| **Spring Data JPA** | - | Truy cập cơ sở dữ liệu |
| **Thymeleaf** | - | Template engine HTML |
| **MySQL** | 8.0 | Cơ sở dữ liệu |
| **Hibernate** | 7.2.12 | ORM (Object-Relational Mapping) |
| **Lombok** | - | Giảm boilerplate code |
| **Apache POI** | 5.2.3 | Xuất Excel |
| **OpenPDF** | 1.3.30 | Xuất PDF |
| **Java** | 17+ | Ngôn ngữ lập trình |

---

## 🏗️ II. CẤU TRÚC THƯ MỤC DỰ ÁN

```
web_dat_ban/
├── src/main/
│   ├── java/vn/edu/ptit/restaurant/
│   │   ├── RestaurantApplication.java          ← Entry Point
│   │   ├── controller/                         ← Controllers (xử lý request)
│   │   │   ├── AdminMenuController.java
│   │   │   ├── AdminTableController.java
│   │   │   ├── AdminReportController.java
│   │   │   ├── AuthController.java
│   │   │   ├── CustomerController.java
│   │   │   ├── HomeController.java
│   │   │   └── StaffOrderController.java
│   │   ├── entity/                            ← Các lớp Entity (tương ứng bảng DB)
│   │   │   ├── User.java
│   │   │   ├── MenuItem.java
│   │   │   ├── Order.java
│   │   │   ├── OrderItem.java
│   │   │   ├── Reservation.java
│   │   │   ├── DiningTable.java
│   │   │   ├── Category.java
│   │   │   ├── Area.java
│   │   │   └── enums/ (OrderStatus, Role, TableStatus...)
│   │   ├── repository/                        ← JPA Repositories (truy vấn DB)
│   │   │   ├── UserRepository.java
│   │   │   ├── MenuItemRepository.java
│   │   │   ├── OrderRepository.java
│   │   │   ├── ReservationRepository.java
│   │   │   └── ...
│   │   ├── service/                           ← Business Logic
│   │   │   ├── UserService.java
│   │   │   ├── MenuItemService.java
│   │   │   ├── CartService.java
│   │   │   ├── OrderService.java
│   │   │   ├── ReservationService.java
│   │   │   └── impl/ (các implement)
│   │   ├── dto/                               ← Data Transfer Objects
│   │   │   ├── CartItem.java
│   │   │   └── ReportDTO.java
│   │   └── security/                          ← Spring Security config
│   │       ├── SecurityConfig.java
│   │       ├── CustomUserDetailsService.java
│   │       └── CustomAuthenticationSuccessHandler.java
│   └── resources/
│       ├── application.properties              ← Config local
│       ├── application-docker.properties       ← Config Docker
│       ├── static/                            ← CSS, JS, Images
│       └── templates/                         ← Thymeleaf HTML templates
│           ├── index.html
│           ├── login.html
│           ├── register.html
│           ├── admin/
│           ├── customer/
│           └── staff/
├── pom.xml                                    ← Maven dependencies
├── docker-compose.yml                        ← Docker configuration
└── Dockerfile                                ← Build Docker image
```

---

## 🔄 III. LUỒNG HOẠT ĐỘNG CỦA ỨNG DỤNG

### 3.1 Khởi động ứng dụng (Startup Flow)

```
1. RestaurantApplication.main() khởi chạy
    ↓
2. Spring Boot khởi tạo ApplicationContext
    ↓
3. SecurityConfig được load - cấu hình Spring Security
    ↓
4. Scan tất cả @Controller, @Service, @Repository
    ↓
5. JPA scan tất cả @Entity
    ↓
6. Hibernate tạo kết nối đến MySQL
    ↓
7. JPA repositories được khởi tạo (8 cái scan được)
    ↓
8. Tomcat server khởi động trên port 8080
    ↓
9. Ứng dụng sẵn sàng nhận request
```

### 3.2 Authentication & Authorization Flow

#### **Quy trình đăng nhập:**

```
1. User truy cập POST /login (gửi username + password)
    ↓
2. Spring Security chặn, gọi CustomUserDetailsService.loadUserByUsername()
    ↓
3. UserRepository tìm User trong DB
    ↓
4. So sánh password mã hóa (BCrypt)
    ↓
5. Nếu đúng → CustomAuthenticationSuccessHandler.onAuthenticationSuccess()
    ↓
6. Kiểm tra role (ADMIN, STAFF hay CUSTOMER)
    ↓
7. Redirect đến trang phù hợp:
   - ADMIN → /admin/menu
   - STAFF → /staff/orders
   - CUSTOMER → /
```

#### **Quy trình đăng ký:**

```
GET /register
    ↓
Hiển thị form register.html với object User mới
    ↓
POST /register (user điền form)
    ↓
AuthController.processRegistration() xử lý
    ↓
Kiểm tra username có trùng không (UserRepository.findByUsername)
    ↓
Nếu trùng → return register.html với error
Nếu không trùng → UserService.registerNewUserAccount()
    ↓
Mã hóa password bằng BCryptPasswordEncoder
    ↓
Lưu User vào DB với role CUSTOMER
    ↓
Redirect → /login?registered=true
```

#### **Spring Security Access Control:**

```java
- "/" → Tất cả được phép
- "/login", "/register" → Tất cả được phép
- "/css/**", "/js/**", "/images/**" → Tất cả được phép (static files)
- "/admin/**" → Chỉ ROLE_ADMIN
- "/staff/**" → ROLE_ADMIN hoặc ROLE_STAFF
- "/customer/**" → Tất cả user đã authenticated
- Các endpoint khác → phải authenticated
```

---

## 🛒 IV. LUỒNG CHÍNH CỦA CÁC TÍNH NĂNG

### 4.1 CUSTOMER - Xem Menu & Đặt Hàng

```
GET /menu (CustomerController.viewMenu)
    ├─ CategoryService.findAll() → danh sách category
    ├─ MenuItemService.findAll() → danh sách food
    ├─ CartService.getCount() → số lượng item trong cart (Session)
    └─ return "customer/menu"

POST /cart/add (addToCart)
    ├─ MenuItemService.findById(menuItemId)
    ├─ Tạo CartItem object
    ├─ CartService.add(cartItem) → lưu vào Session
    ├─ Set flash attribute "success"
    └─ Redirect → /menu

GET /cart (viewCart)
    ├─ CartService.getItems() → lấy từ Session
    ├─ CartService.getAmount() → tính tổng tiền
    └─ return "customer/cart"

POST /cart/update
    ├─ Cập nhật quantity trong Session
    └─ Redirect → /cart

GET /cart/remove/{id}
    ├─ CartService.remove(id)
    └─ Redirect → /cart
```

### 4.2 CUSTOMER - Đặt Bàn (Reservation)

```
GET /reservation (viewReservationForm)
    ├─ Kiểm tra nếu user chưa login → redirect /login
    ├─ DiningTableService.findAll() → danh sách bàn có sẵn
    └─ return "customer/reservation" (form)

POST /reservation (submitReservation)
    ├─ Lấy username từ Principal (từ Spring Security)
    ├─ UserService.findByUsername() → tìm User object
    ├─ Parse reservationDate + reservationTime thành LocalDateTime
    ├─ ReservationService.createReservation()
    │   ├─ Tạo object Reservation
    │   ├─ Set status = PENDING
    │   ├─ Thêm @Version cho optimistic locking (tránh race condition)
    │   ├─ ReservationRepository.save()
    │   └─ Lưu vào DB
    ├─ Catch ObjectOptimisticLockingFailureException nếu bàn vừa bị đặt
    ├─ Set success/error message
    └─ Redirect → /my-reservations

GET /my-reservations (myReservations)
    ├─ Lấy user từ Principal
    ├─ ReservationService.findByUserId() → list reservations
    └─ return "customer/my-reservations"

POST /reservation/cancel (cancelReservation)
    ├─ ReservationService.cancelReservation()
    │   └─ Cập nhật status = CANCELLED
    ├─ Lưu vào DB
    ├─ Set success message
    └─ Redirect → /my-reservations
```

### 4.3 ADMIN - Quản lý Menu

```
GET /admin/menu (AdminMenuController.index)
    ├─ CategoryService.findAll() → danh sách danh mục
    ├─ MenuItemService.findAll() → danh sách món ăn
    └─ return "admin/menu/index"

[CATEGORY MANAGEMENT]

GET /admin/menu/category/add
    ├─ Tạo Category object mới
    └─ return "admin/menu/category-form"

POST /admin/menu/category/save (saveCategory)
    ├─ CategoryService.save(category)
    │   └─ CategoryRepository.save()
    ├─ Lưu vào DB
    └─ Redirect → /admin/menu

GET /admin/menu/category/edit/{id}
    ├─ CategoryService.findById(id)
    ├─ Add vào model
    └─ return "admin/menu/category-form"

GET /admin/menu/category/delete/{id}
    ├─ CategoryService.deleteById(id)
    │   └─ CategoryRepository.deleteById()
    └─ Redirect → /admin/menu

[MENU ITEM MANAGEMENT]

GET /admin/menu/item/add
    ├─ Tạo MenuItem object mới
    ├─ CategoryService.findAll() → danh sách category (select dropdown)
    └─ return "admin/menu/item-form"

POST /admin/menu/item/save (saveMenuItem)
    ├─ MenuItemService.save(menuItem)
    │   ├─ MenuItemRepository.save()
    │   └─ Lưu vào DB (ghi field category_id)
    └─ Redirect → /admin/menu

GET /admin/menu/item/edit/{id}
    ├─ MenuItemService.findById(id)
    ├─ CategoryService.findAll()
    ├─ Add vào model
    └─ return "admin/menu/item-form"

GET /admin/menu/item/delete/{id}
    ├─ MenuItemService.deleteById(id)
    │   └─ MenuItemRepository.deleteById()
    └─ Redirect → /admin/menu
```

### 4.4 ADMIN - Quản lý Bàn Ăn

```
GET /admin/table (AdminTableController)
    ├─ AreaService.findAll() → danh sách khu vực (VIP, thường...)
    ├─ DiningTableService.findAll() → danh sách bàn
    └─ return "admin/table/index"

GET /admin/table/add
    ├─ AreaService.findAll() → cho select
    └─ return "admin/table/form"

POST /admin/table/save
    ├─ DiningTableService.save(table)
    └─ Redirect → /admin/table

GET /admin/table/edit/{id}
    ├─ DiningTableService.findById(id)
    └─ return "admin/table/form"

GET /admin/table/delete/{id}
    ├─ DiningTableService.deleteById(id)
    └─ Redirect → /admin/table
```

### 4.5 STAFF - Quản lý Đơn Hàng

```
GET /staff/orders (StaffOrderController)
    ├─ OrderService.findAll() → danh sách order
    ├─ ReservationService.findAll() → danh sách reservation
    └─ return "staff/orders/index"

POST /staff/order/{id}/confirm
    ├─ OrderService.updateStatus(id, CONFIRMED)
    ├─ Set table status = OCCUPIED
    └─ Redirect → /staff/orders

POST /staff/order/{id}/complete
    ├─ OrderService.updateStatus(id, COMPLETED)
    ├─ Tính total amount từ OrderItems
    ├─ Set table status = AVAILABLE
    └─ Redirect → /staff/orders
```

### 4.6 ADMIN - Báo Cáo & Xuất File

```
GET /admin/report
    ├─ ReportService.generateReport()
    │   ├─ OrderRepository.findAll()
    │   ├─ Tính tổng doanh thu
    │   ├─ Tính số đơn trung bình
    │   └─ Tạo ReportDTO
    └─ return "admin/report/index"

GET /admin/report/export/excel
    ├─ ExcelExportService.export()
    │   ├─ Dùng Apache POI tạo .xlsx
    │   ├─ Thêm headers, data rows
    │   └─ Write to OutputStream
    ├─ Set response header: application/vnd.openxmlformats
    └─ Download file

GET /admin/report/export/pdf
    ├─ PdfExportService.export()
    │   ├─ Dùng OpenPDF tạo .pdf
    │   ├─ Thêm tables, formatted text
    │   └─ Write to OutputStream
    ├─ Set response header: application/pdf
    └─ Download file
```

---

## 📊 V. CÁC ENTITY VÀ QUAN HỆ DATABASE

### 5.1 Entity Diagrams

```
┌─────────────────────────────────────────────────────────────────┐
│                         DATABASE SCHEMA                         │
└─────────────────────────────────────────────────────────────────┘

[users] (1)
  ├─ id (PK)
  ├─ username (UNIQUE)
  ├─ password (encrypted BCrypt)
  ├─ full_name
  ├─ phone
  ├─ email
  ├─ role (ENUM: ADMIN, STAFF, CUSTOMER)
  └─ created_at

        ↓ (1:n)

[reservations] (n)
  ├─ id (PK)
  ├─ user_id (FK) → users.id
  ├─ table_id (FK) → tables.id
  ├─ reservation_time
  ├─ number_of_guests
  ├─ note
  ├─ status (ENUM: PENDING, CONFIRMED, CANCELLED)
  ├─ created_at
  └─ version (optimistic locking)

        ↓ (1:n)

[orders] (n)
  ├─ id (PK)
  ├─ table_id (FK) → tables.id
  ├─ user_id (FK) → users.id (staff)
  ├─ reservation_id (FK) → reservations.id (nullable)
  ├─ total_amount
  ├─ status (ENUM: PENDING, CONFIRMED, COMPLETED, CANCELLED)
  └─ created_at

        ↓ (1:n)

[order_items] (n)
  ├─ id (PK)
  ├─ order_id (FK) → orders.id
  ├─ menu_item_id (FK) → menu_items.id
  ├─ quantity
  ├─ price (giá tại thời điểm order)
  └─ sub_total

        ↓ (n:1)

[menu_items]
  ├─ id (PK)
  ├─ category_id (FK) → categories.id
  ├─ name
  ├─ description
  ├─ price
  ├─ image_url
  └─ is_available (BOOLEAN)

        ↓ (n:1)

[categories]
  ├─ id (PK)
  ├─ name
  └─ description

[tables]
  ├─ id (PK)
  ├─ area_id (FK) → areas.id
  ├─ table_name
  ├─ capacity
  ├─ status (ENUM: AVAILABLE, OCCUPIED, RESERVED)
  └─ version (optimistic locking)

        ↓ (n:1)

[areas]
  ├─ id (PK)
  └─ name
```

### 5.2 Các Enum quan trọng

```
Role:
  - ADMIN: Quản lý system
  - STAFF: Nhân viên nhà hàng
  - CUSTOMER: Khách hàng

OrderStatus:
  - PENDING: Chờ xác nhận
  - CONFIRMED: Đã xác nhận
  - COMPLETED: Hoàn thành
  - CANCELLED: Hủy

ReservationStatus:
  - PENDING: Chờ xác nhận
  - CONFIRMED: Đã xác nhận
  - CANCELLED: Đã hủy

TableStatus:
  - AVAILABLE: Sẵn sàng
  - OCCUPIED: Đang sử dụng
  - RESERVED: Đã được đặt
```

---

## 🔐 VI. SPRING SECURITY CONFIGURATION

### 6.1 Security Config Details

```java
// SecurityConfig.java
→ PasswordEncoder: BCryptPasswordEncoder (mã hóa mật khẩu)
→ AuthenticationManager: ProviderManager + DaoAuthenticationProvider
→ SecurityFilterChain: Quy tắc HTTP security

Public Endpoints:
  - "/" (trang chủ)
  - "/login" (đăng nhập)
  - "/register" (đăng ký)
  - "/css/**", "/js/**", "/images/**" (tài nguyên tĩnh)

Protected Endpoints by Role:
  - "/admin/**" → @hasRole('ADMIN')
  - "/staff/**" → @hasAnyRole('ADMIN', 'STAFF')
  - "/customer/**" → @hasAnyRole('ADMIN', 'STAFF', 'CUSTOMER')
  - Các endpoint khác → phải authenticated

Form Login:
  - loginPage: /login
  - successHandler: CustomAuthenticationSuccessHandler
    (redirect dựa vào role)
  - logout: /logout (xóa session)
```

### 6.2 CustomUserDetailsService

```
loadUserByUsername(username) {
    ├─ UserRepository.findByUsername(username)
    ├─ Nếu không tìm thấy → throw UsernameNotFoundException
    ├─ Nếu tìm thấy → tạo UserDetails object
    │   ├─ username
    │   ├─ password (mã hóa)
    │   └─ authorities (roles)
    └─ Return UserDetails
}
```

---

## 🎯 VII. CARTSERVICE (SESSION SCOPE)

```
CartService:
  - Scope: @SessionScope → mỗi user có 1 cart riêng (trong session)
  - Data: Map<Long, CartItem> (menuItemId → CartItem)

Methods:
  - add(CartItem): Thêm hoặc tăng quantity
  - remove(Long menuItemId): Xóa item
  - update(Long menuItemId, int quantity): Cập nhật quantity
  - clear(): Xóa toàn bộ cart
  - getItems(): Trả về collection CartItem
  - getCount(): Tổng quantity của tất cả items
  - getAmount(): Tính tổng tiền (price × quantity)
```

---

## 🏃 VIII. CÁCH CHẠY DỰ ÁN

### 8.1 Chạy với MySQL local

#### Yêu cầu:
- Java 17+ đã cài đặt
- MySQL 8.0 đã chạy
- Database "web_dat_ban" đã tạo

#### Bước 1: Cập nhật cấu hình MySQL
```ini
# File: src/main/resources/application.properties

spring.datasource.url=jdbc:mysql://localhost:3306/web_dat_ban?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root       # ← Sửa username của bạn
spring.datasource.password=root       # ← Sửa password của bạn
```

#### Bước 2: Build & Run

**Option A: Dùng Maven wrapper (không cần cài Maven)**
```bash
# Build (tạo jar)
./mvnw clean package -DskipTests

# Run
java -jar target/restaurant-0.0.1-SNAPSHOT.jar
```

**Option B: Dùng Spring Boot Maven plugin**
```bash
./mvnw spring-boot:run
```

#### Bước 3: Truy cập
```
http://localhost:8080
```

### 8.2 Chạy với Docker Compose (EASY MODE)

#### Yêu cầu:
- Docker đã cài đặt
- Docker Compose đã cài đặt

#### Bước 1: Build & Run

```bash
# CD vào thư mục project
cd /Volumes/study/laptrinhweb/web_dat_ban

# Build image & start containers
docker-compose up --build

# (hoặc chạy background)
docker-compose up -d --build
```

#### Bước 2: Kiểm tra logs
```bash
docker-compose logs -f app     # logs của ứng dụng
docker-compose logs -f db      # logs của MySQL
```

#### Bước 3: Truy cập
```
http://localhost:8080
```

#### Bước 4: Dừng
```bash
docker-compose down
```

---

## 📝 IX. CÁC FILE QUAN TRỌNG VÀ GIẢI THÍCH

| File | Tác dụng |
|------|---------|
| **RestaurantApplication.java** | Entry point, khởi động ứng dụng |
| **pom.xml** | Quản lý dependencies, build configuration |
| **application.properties** | Config local (DB, port, Thymeleaf...) |
| **application-docker.properties** | Config khi chạy Docker |
| **SecurityConfig.java** | Cấu hình Spring Security, phân quyền |
| **CustomUserDetailsService.java** | Load thông tin user từ DB |
| **CustomAuthenticationSuccessHandler.java** | Xử lý redirect sau login theo role |
| **CartService.java** | Quản lý giỏ hàng (Session scope) |
| **User.java** | Entity user, 3 role: ADMIN, STAFF, CUSTOMER |
| **MenuItem.java** | Entity món ăn |
| **Order.java** | Entity đơn đặt |
| **OrderItem.java** | Entity chi tiết đơn (1 order có nhiều items) |
| **Reservation.java** | Entity đặt bàn |
| **DiningTable.java** | Entity bàn ăn |
| **Category.java** | Entity danh mục (Khai vị, Chính, Tráng miệng...) |
| **Area.java** | Entity khu vực (VIP, Thường...) |
| **AuthController.java** | Xử lý login, register |
| **HomeController.java** | Trang chủ |
| **CustomerController.java** | Xem menu, đặt bàn, giỏ hàng |
| **AdminMenuController.java** | Quản lý menu, category |
| **AdminTableController.java** | Quản lý bàn, khu vực |
| **AdminReportController.java** | Báo cáo, xuất Excel/PDF |
| **StaffOrderController.java** | Xem & xử lý đơn hàng |
| **UserService.java** | Business logic xử lý user |
| **MenuItemService.java** | Business logic xử lý menu |
| **OrderService.java** | Business logic xử lý đơn đặt |
| **ReservationService.java** | Business logic xử lý reservation |
| **Dockerfile** | Build Docker image |
| **docker-compose.yml** | Orchestrate app + MySQL containers |

---

## 💡 X. NHỮNG ĐIỂM CẦN NHỚ KHI ÔN

### 1. **Luồng Đặt Bàn (Race Condition)**
```
- Dùng @Version trong Reservation & DiningTable
- Optimistic Locking: Nếu 2 người cùng đặt bàn
  → Người thứ 2 sẽ catch ObjectOptimisticLockingFailureException
  → Show lỗi: "Bàn vừa được đặt, hãy chọn bàn khác"
```

### 2. **Session Scope Cart**
```
- CartService dùng @SessionScope
- Mỗi user có 1 instance riêng (lưu trong session)
- Khi logout → session xóa → cart xóa
- Không cần lưu DB, giúp performance tốt
```

### 3. **Spring Security Interceptor**
```
- Request → DispatcherServlet → SecurityFilterChain
- Nếu chưa login → redirect /login
- Nếu role không đủ → HTTP 403 Forbidden
- Nếu hợp lệ → pass qua controller
```

### 4. **Thymeleaf Template Engine**
```
- Template files nằm ở: src/main/resources/templates/
- Spring tự động map @GetMapping("/page") → templates/page.html
- Có thể dùng th:if, th:for, th:each để render dynamic content
- Access data từ Model object
```

### 5. **JPA & Hibernate**
```
- Entity class = Table
- @ManyToOne, @OneToMany = Foreign key relationships
- @Enumerated(EnumType.STRING) = Lưu enum dưới dạng string trong DB
- @PrePersist = Hook trước khi insert (set created_at)
- @Version = Optimistic locking
```

### 6. **Validation & Error Handling**
```
- Nếu DB error → Spring throw exception
- Controller catch → set error message vào model
- Template hiển thị error message cho user
- Ví dụ: "Bàn vừa được đặt, chọn bàn khác"
```

### 7. **Repository Pattern**
```
- UserRepository.findByUsername() → custom query
- MenuItemRepository.findAll() → inherit from JpaRepository
- Tất cả query được Hibernate tự động translate thành SQL
```

---

## ❓ XI. CÂU HỎI THỨ VĀN ĐÁP CÓ KHẢ NĂNG XUẤT HIỆN

### Q1: Dự án có mấy vai trò, mỗi vai trò có quyền gì?
**A:**
```
3 vai trò:
1. ADMIN
   - Quản lý menu (CRUD category, menu items)
   - Quản lý bàn (CRUD tables, areas)
   - Xem báo cáo doanh thu
   - Xuất Excel, PDF

2. STAFF
   - Xem danh sách đơn đặt
   - Xác nhận/hoàn thành đơn hàng
   - Quản lý table status (AVAILABLE/OCCUPIED)

3. CUSTOMER
   - Xem menu
   - Thêm vào giỏ hàng
   - Đặt bàn trước
   - Xem lịch sử đặt bàn
```

### Q2: Khi user đặt bàn, nếu 2 người cùng đặt bàn này thì sao?
**A:**
```
Dùng Optimistic Locking (@Version):
1. Người A đặt bàn B (version = 0)
2. Người B cũng đặt bàn B (version = 0)
3. Người A commit trước → version bàn B thành 1
4. Người B commit → version khác (0 vs 1) → fail
5. Throw ObjectOptimisticLockingFailureException
6. Controller catch → show "Bàn vừa được đặt"
```

### Q3: Cart được lưu ở đâu? Khi đóng browser thì mất không?
**A:**
```
Cart được lưu trong HTTP Session (memory của server):
- Khi user thêm item → CartService.add() lưu vào session
- Khi close browser → session timeout → cart xóa
- Tuy nhiên nếu user vẫn online, session vẫn giữ
- Ưu điểm: nhanh, không cần query DB
- Nhược điểm: nếu server restart → mất cart
```

### Q4: Password được mã hóa như nào?
**A:**
```
Dùng BCryptPasswordEncoder:
1. User register → input password "123456"
2. BCrypt mã hóa → "$2a$10$... (64 ký tự)"
3. Lưu hash vào DB (không lưu password gốc)
4. Khi login → BCrypt so sánh password input với hash
5. Nếu match → login OK
Ưu điểm: không thể giải mã, chỉ có thể so sánh
```

### Q5: Làm sao để thêm role mới (ví dụ MANAGER)?
**A:**
```
1. Thêm value vào enum Role:
   public enum Role {
       ADMIN, STAFF, CUSTOMER, MANAGER
   }

2. Sửa SecurityConfig.java:
   .requestMatchers("/manager/**").hasRole("MANAGER")

3. Tạo controller: ManagerController.java

4. Cập nhật DB: UPDATE users SET role = 'MANAGER' WHERE id = 1;

5. Khi user login → Spring sẽ gán role này tự động
```

### Q6: Nếu muốn lưu cart vào DB thì làm sao?
**A:**
```
1. Tạo entity CartHistory
   - user_id, menu_item_id, quantity, created_at

2. Thay CartService từ @SessionScope → @Service normal

3. Khi addToCart:
   CartService.add() → CartHistoryRepository.save()

4. Khi logout, lại có thể load cart từ DB

5. Nhưng phức tạp hơn, không cần thiết với demo
```

### Q7: Dự án có API REST không? Hay chỉ web MVC?
**A:**
```
Dự án hiện tại chỉ có WEB MVC:
- Model + View + Controller (Template-based)
- Response là HTML (Thymeleaf)
- Không có @RestController (không trả JSON)

Nếu muốn làm API REST:
1. Thêm RestController
2. Trả @ResponseBody JSON
3. Swagger/SpringDoc cho API docs
```

### Q8: Làm sao để thêm image cho menu item?
**A:**
```
1. Thêm field imageUrl (String) trong MenuItem.java ✓ đã có

2. Frontend: <input type="file"> upload image

3. Controller xử lý file:
   MultipartFile image = request.getFile("image")
   String filename = image.getOriginalFilename()
   image.transferTo(new File("src/main/resources/static/images/" + filename))

4. Lưu filename vào MenuItem.imageUrl

5. Template hiển thị: <img th:src="@{/images/{filename}(filename=${item.imageUrl})}">
```

### Q9: Làm sao để xuất báo cáo Excel?
**A:**
```
1. AdminReportController.exportExcel()

2. ReportService.generateReport()
   ├─ Query OrderRepository.findAll()
   ├─ Tính toán doanh thu, số đơn...
   ├─ Tạo ReportDTO object

3. ExcelExportService.export()
   ├─ Import Apache POI library ✓ đã có
   ├─ new XSSFWorkbook() tạo workbook
   ├─ Thêm sheet, row, cell
   ├─ Set style, format
   ├─ Write to OutputStream

4. Response header:
   response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
   response.setHeader("Content-Disposition", "attachment; filename=report.xlsx")

5. Browser download file
```

### Q10: Khi máy chủ restart, có mất dữ liệu không?
**A:**
```
- DB (MySQL): Không mất (dữ liệu lưu trên disk)
- Session (Cart): Mất hết (lưu trong memory)
- User session login: Mất hết (phải login lại)

Nếu muốn giữ cart khi server restart:
→ Lưu cart vào DB hoặc Redis (cache)
```

---

## 📚 XII. TÓMO TẮT

### Kiến trúc: **MVC (Model-View-Controller)**
- **Model**: Entity + Service (xử lý business logic)
- **View**: Thymeleaf templates (HTML)
- **Controller**: @RequestMapping handlers

### Database: **MySQL 8.0 + Hibernate ORM**
- 8 entities: User, MenuItem, Category, Order, OrderItem, Reservation, DiningTable, Area
- Relationships: @ManyToOne, @OneToMany

### Security: **Spring Security + BCrypt**
- Authentication: User + Password (BCrypt mã hóa)
- Authorization: 3 roles (ADMIN, STAFF, CUSTOMER)
- Protection: @PreAuthorize, @Secured annotations

### Session: **CartService (@SessionScope)**
- Lưu giỏ hàng trong HTTP session
- Mỗi user có instance riêng
- Xóa khi logout

### Main Flow:
```
Request → SecurityFilterChain → Controller → Service → Repository
  ↓ (nếu cần) ↓                                ↓
                                          Database
```

---

## ✅ XIII. CHECKLIST ÔN TẬP

- [ ] Nắm được 3 vai trò (ADMIN, STAFF, CUSTOMER)
- [ ] Hiểu luồng login + đăng ký
- [ ] Biết cách quản lý menu, bàn (CRUD)
- [ ] Hiểu cách đặt bàn + xử lý race condition
- [ ] Biết CartService hoạt động sao (@SessionScope)
- [ ] Nắm Spring Security cấu hình
- [ ] Hiểu Entity relationships (1:n, n:n)
- [ ] Biết cách chạy project (local + Docker)
- [ ] Có thể giải thích file nào làm gì
- [ ] Biết xuất Excel/PDF cơ chế

---

**Good luck với buổi vấn đáp! 🎓**
