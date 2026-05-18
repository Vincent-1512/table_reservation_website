# 💾 DATABASE SCHEMA & ENTITY RELATIONSHIPS

---

## 📊 ER DIAGRAM (Entity Relationship)

```
┌────────────────────────────────────────────────────────────────────────────────────┐
│                          RESTAURANT DATABASE SCHEMA                                │
└────────────────────────────────────────────────────────────────────────────────────┘

                                   ┌──────────────┐
                                   │    users     │
                                   ├──────────────┤
                                   │ id (PK)      │
                                   │ username (U) │
                                   │ password     │
                                   │ full_name    │
                                   │ phone        │
                                   │ email        │
                                   │ role (Enum)  │ ◄──── ADMIN, STAFF, CUSTOMER
                                   │ created_at   │
                                   └──────────────┘
                                         │
                    ┌────────────────────┼────────────────────┐
                    │                    │                    │
                 (1:n)               (1:n)                 (1:n)
                    │                    │                    │
         ┌──────────▼─────────┐  ┌──────▼─────────┐  ┌──────▼─────────┐
         │  reservations      │  │     orders     │  │   (future use) │
         ├────────────────────┤  ├────────────────┤  └────────────────┘
         │ id (PK)            │  │ id (PK)        │
         │ user_id (FK→users) │  │ table_id (FK)  │
         │ table_id (FK)      │  │ user_id (FK)   │
         │ reservation_time   │  │ reservation_id │
         │ number_of_guests   │  │ total_amount   │
         │ note               │  │ status (Enum)  │ ◄──── PENDING, CONFIRMED, COMPLETED, CANCELLED
         │ status (Enum)      │  │ created_at     │
         │ created_at         │  └────────────────┘
         │ version (OL)       │         │
         └────────────────────┘      (1:n)
                    │                   │
                    │            ┌──────▼──────────┐
                    │            │  order_items   │
                    │            ├────────────────┤
                    │            │ id (PK)        │
                    │            │ order_id (FK)  │
                    │            │ menu_item_id   │
                    │            │ quantity       │
                    │            │ price          │
                    │            │ sub_total      │
                    │            └────────────────┘
                    │                   │
                    │                (n:1)
                    │                   │
                    │            ┌──────▼───────────┐
                    │            │   menu_items    │
                    │            ├─────────────────┤
                    │            │ id (PK)         │
                    │            │ category_id(FK) │
                    │            │ name            │
                    │            │ description     │
                    │            │ price           │
                    │            │ image_url       │
                    │            │ is_available    │
                    │            └─────────────────┘
                    │                   │
                    │                (n:1)
                    │                   │
                    │            ┌──────▼──────────┐
                    │            │  categories    │
                    │            ├────────────────┤
                    │            │ id (PK)        │
                    │            │ name           │
                    │            │ description    │
                    │            └────────────────┘
                    │
                 (1:n)
                    │
         ┌──────────▼─────────┐
         │    tables         │
         ├───────────────────┤
         │ id (PK)           │
         │ area_id (FK)      │
         │ table_name        │
         │ capacity          │
         │ status (Enum)     │ ◄──── AVAILABLE, OCCUPIED, RESERVED
         │ version (OL)      │
         └───────────────────┘
                    │
                 (n:1)
                    │
         ┌──────────▼─────────┐
         │     areas         │
         ├───────────────────┤
         │ id (PK)           │
         │ name              │
         └───────────────────┘
```

---

## 🔑 DETAILED SCHEMA

### 1️⃣ **USERS TABLE**

```sql
CREATE TABLE users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    full_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(100),
    role ENUM('ADMIN', 'STAFF', 'CUSTOMER') NOT NULL DEFAULT 'CUSTOMER',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_username (username)
);
```

**Purpose:** Lưu tài khoản user, password mã hóa BCrypt, phân quyền theo role

---

### 2️⃣ **CATEGORIES TABLE**

```sql
CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT
);
```

**Purpose:** Danh mục món ăn (Khai vị, Chính, Tráng miệng, Đồ uống)

---

### 3️⃣ **MENU_ITEMS TABLE**

```sql
CREATE TABLE menu_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_id INT NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    price DECIMAL(10, 2) NOT NULL,
    image_url VARCHAR(255),
    is_available BOOLEAN NOT NULL DEFAULT TRUE,
    FOREIGN KEY (category_id) REFERENCES categories(id),
    INDEX idx_category (category_id)
);
```

**Purpose:** Menu items (Phở, Bánh mì, Cơm tấm...), giá, hình ảnh

**Relationship:** n:1 → categories (nhiều items thuộc 1 category)

---

### 4️⃣ **AREAS TABLE**

```sql
CREATE TABLE areas (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);
```

**Purpose:** Khu vực nhà hàng (VIP, Thường, Ngoài trời)

---

### 5️⃣ **TABLES TABLE**

```sql
CREATE TABLE tables (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    area_id INT NOT NULL,
    table_name VARCHAR(50) NOT NULL,
    capacity INT NOT NULL,
    status ENUM('AVAILABLE', 'OCCUPIED', 'RESERVED') NOT NULL DEFAULT 'AVAILABLE',
    version BIGINT DEFAULT 0,  -- Optimistic Locking
    FOREIGN KEY (area_id) REFERENCES areas(id),
    INDEX idx_area (area_id),
    INDEX idx_status (status)
);
```

**Purpose:** Bàn ăn, số chỗ, khu vực, trạng thái

**Optimistic Locking:** @Version → tránh race condition khi 2 người đặt cùng bàn

---

### 6️⃣ **RESERVATIONS TABLE**

```sql
CREATE TABLE reservations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    table_id BIGINT NOT NULL,
    reservation_time DATETIME NOT NULL,
    number_of_guests INT NOT NULL,
    note TEXT,
    status ENUM('PENDING', 'CONFIRMED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,  -- Optimistic Locking
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (table_id) REFERENCES tables(id),
    INDEX idx_user (user_id),
    INDEX idx_table (table_id),
    INDEX idx_status (status)
);
```

**Purpose:** Lưu lịch sử đặt bàn của khách

**Relationships:**
- n:1 → users (nhiều reservation thuộc 1 user)
- n:1 → tables (nhiều reservation cho 1 bàn)

---

### 7️⃣ **ORDERS TABLE**

```sql
CREATE TABLE orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    table_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    reservation_id BIGINT,  -- Nullable nếu khách đến trực tiếp
    total_amount DECIMAL(10, 2) NOT NULL DEFAULT 0,
    status ENUM('PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (table_id) REFERENCES tables(id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (reservation_id) REFERENCES reservations(id),
    INDEX idx_table (table_id),
    INDEX idx_user (user_id),
    INDEX idx_status (status)
);
```

**Purpose:** Đơn đặt hàng (order), liên kết với bàn, user (staff), reservation, tổng tiền

**Relationships:**
- n:1 → tables
- n:1 → users (nhân viên thực hiện)
- n:1 → reservations (optional)

---

### 8️⃣ **ORDER_ITEMS TABLE**

```sql
CREATE TABLE order_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    menu_item_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    price DECIMAL(10, 2) NOT NULL,  -- Giá tại thời điểm order
    sub_total DECIMAL(10, 2) NOT NULL,  -- price * quantity
    FOREIGN KEY (order_id) REFERENCES orders(id),
    FOREIGN KEY (menu_item_id) REFERENCES menu_items(id),
    INDEX idx_order (order_id),
    INDEX idx_menu_item (menu_item_id)
);
```

**Purpose:** Chi tiết từng item trong order (1 order có nhiều items)

**Relationships:**
- n:1 → orders
- n:1 → menu_items

---

## 🔍 RELATIONSHIPS SUMMARY

| Relationship | Chi tiết |
|-------------|---------|
| users → reservations | 1:n (1 user đặt nhiều bàn) |
| users → orders | 1:n (1 staff thực hiện nhiều order) |
| tables → reservations | 1:n (1 bàn được đặt nhiều lần) |
| tables → orders | 1:n (1 bàn có nhiều order) |
| areas → tables | 1:n (1 khu vực có nhiều bàn) |
| categories → menu_items | 1:n (1 danh mục có nhiều item) |
| orders → order_items | 1:n (1 order có nhiều items) |
| menu_items → order_items | 1:n (1 item được order nhiều lần) |
| reservations → orders | 1:1 (1 reservation → 1 order, optional) |

---

## 📝 JAVA ENTITY ANNOTATIONS

```java
// 1:n Relationship
@ManyToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "user_id", nullable = false)
private User user;

// 1:1 Relationship
@OneToOne(fetch = FetchType.LAZY)
@JoinColumn(name = "reservation_id")
private Reservation reservation;

// Enum stored as String
@Enumerated(EnumType.STRING)
@Column(nullable = false)
private Role role;

// Optimistic Locking (tránh race condition)
@Version
private Long version;

// Auto-set created_at
@PrePersist
protected void onCreate() {
    createdAt = LocalDateTime.now();
}
```

---

## 🔐 INDEXES (Tối ưu Query)

```sql
-- Tìm user by username
CREATE INDEX idx_username ON users(username);

-- Filter orders by status
CREATE INDEX idx_status ON orders(status);

-- Tìm reservations of user
CREATE INDEX idx_user ON reservations(user_id);

-- Tìm tables in area
CREATE INDEX idx_area ON tables(area_id);
```

---

## 💪 ENUMS IN JAVA

```java
// Role.java
public enum Role {
    ADMIN,      // Quản lý hệ thống
    STAFF,      // Nhân viên nhà hàng
    CUSTOMER    // Khách hàng
}

// OrderStatus.java
public enum OrderStatus {
    PENDING,    // Chờ xác nhận
    CONFIRMED,  // Đã xác nhận
    COMPLETED,  // Hoàn thành (đã thanh toán)
    CANCELLED   // Bị hủy
}

// ReservationStatus.java
public enum ReservationStatus {
    PENDING,    // Chờ xác nhận
    CONFIRMED,  // Đã xác nhận
    CANCELLED   // Bị hủy
}

// TableStatus.java
public enum TableStatus {
    AVAILABLE,  // Sẵn sàng
    OCCUPIED,   // Đang sử dụng
    RESERVED    // Đã được đặt
}
```

---

## 📊 DATA FLOW EXAMPLE

### Kịch bản: Customer đặt bàn & gọi đồ ăn

```
1. User register/login
   ├─ INSERT users (role = CUSTOMER)
   └─ Password được mã hóa BCrypt

2. User xem menu
   ├─ SELECT menu_items JOIN categories
   └─ CartService lưu vào session

3. User đặt bàn (Reservation)
   ├─ INSERT reservations (user_id, table_id, status=PENDING)
   ├─ UPDATE tables SET status='RESERVED'
   └─ version = 1 (optimistic lock)

4. Staff xác nhận reservation
   ├─ UPDATE reservations SET status='CONFIRMED'
   └─ User nhận thông báo

5. Customer gọi đồ ăn → Staff tạo order
   ├─ INSERT orders (table_id, user_id, status=PENDING)
   ├─ INSERT order_items (order_id, menu_item_id, quantity, price)
   ├─ INSERT order_items (order_id, menu_item_id, quantity, price)
   ├─ UPDATE orders SET total_amount = sum(price × quantity)
   └─ CartService.clear() (xóa session cart)

6. Staff hoàn thành order
   ├─ UPDATE orders SET status='COMPLETED'
   ├─ UPDATE tables SET status='AVAILABLE'
   └─ Customer thanh toán & rời đi
```

---

## 🔒 OPTIMISTIC LOCKING EXAMPLE

```java
// Situation: 2 customers order same table at same time

// Thread 1 (Customer A)
Reservation res1 = new Reservation();
res1.setTableId(5);
res1.setVersion(0);  // Load from DB
reservationRepository.save(res1);  // UPDATE ... WHERE version = 0
// ✅ Success! version → 1

// Thread 2 (Customer B)
Reservation res2 = new Reservation();
res2.setTableId(5);
res2.setVersion(0);  // Load from DB (stale)
reservationRepository.save(res2);  // UPDATE ... WHERE version = 0
// ❌ FAIL! No rows updated (version already 1)
// Throw: ObjectOptimisticLockingFailureException
// → Controller catch & show: "Bàn vừa được đặt"
```

---

## 📈 QUERY EXAMPLES

```sql
-- Tìm tất cả menu item của category "Khai vị"
SELECT * FROM menu_items 
WHERE category_id = (SELECT id FROM categories WHERE name = 'Khai vị');

-- Tìm tất cả reservation chưa confirm
SELECT * FROM reservations WHERE status = 'PENDING';

-- Tính tổng doanh thu hôm nay
SELECT SUM(total_amount) FROM orders 
WHERE DATE(created_at) = CURDATE() AND status = 'COMPLETED';

-- Tìm bàn đang trống
SELECT * FROM tables WHERE status = 'AVAILABLE';

-- Tìm những order có >5 items
SELECT o.id, COUNT(oi.id) as item_count 
FROM orders o 
JOIN order_items oi ON o.id = oi.order_id 
GROUP BY o.id 
HAVING COUNT(oi.id) > 5;
```

---

**Hiểu database = Hiểu dự án 70%! 📚**
