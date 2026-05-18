# Lumina Restaurant - Hệ Thống Đặt Bàn & Menu Điện Tử

Chào mừng bạn đến với dự án **Lumina Restaurant**, một ứng dụng web quản lý nhà hàng hiện đại được xây dựng bằng **Spring Boot**, **Thymeleaf** và **MySQL**. Dự án được thiết kế theo kiến trúc 3 lớp (3-Tier Architecture) và tuân thủ nghiêm ngặt mô hình MVC thuần (Server-Side Rendering).

## 🚀 Tính Năng Chính

### 1. Dành cho Khách Hàng (Customer)
*   **Xem Thực đơn:** Menu điện tử hiện đại, phân loại theo danh mục.
*   **Giỏ hàng:** Thêm món ăn vào giỏ hàng (sử dụng Session).
*   **Đặt bàn trực tuyến:** Chọn bàn, khu vực và thời gian đặt bàn.
*   **Chống kẹt bàn:** Sử dụng **Optimistic Locking (@Version)** để ngăn chặn xung đột khi nhiều người đặt cùng một bàn một lúc.
*   **Quản lý lịch sử:** Xem danh sách bàn đã đặt và trạng thái.

### 2. Dành cho Nhân viên (Staff)
*   **Sơ đồ bàn trực quan:** Quản lý trạng thái bàn (Trống, Đang sử dụng, Đã đặt).
*   **Quản lý đơn hàng:** Mở bàn, thêm món, cập nhật hóa đơn.
*   **Thanh toán:** Tự động tính tiền và in hóa đơn (lưu giá tại thời điểm gọi món).

### 3. Dành cho Quản trị viên (Admin)
*   **Quản lý thực đơn:** CRUD Món ăn và Danh mục.
*   **Quản lý cơ sở vật chất:** CRUD Khu vực và Bàn ăn.
*   **Báo cáo & Thống kê:** Theo dõi doanh thu, số đơn hàng.
*   **Xuất file chuyên nghiệp:** Xuất báo cáo doanh thu ra **Excel (.xlsx)** và danh sách đặt bàn ra **PDF**.

---

## 🛠 Công Nghệ Sử Dụng
*   **Backend:** Java 17, Spring Boot 3, Spring Data JPA, Spring Security.
*   **Frontend:** Thymeleaf, Vanilla CSS (Glassmorphism design), JavaScript.
*   **Database:** MySQL 8.0.
*   **Tools:** Docker & Docker Compose, Maven, Apache POI (Excel), OpenPDF (PDF).

---

## 📦 Hướng Dẫn Cài Đặt

### Cách 1: Chạy bằng Docker (Khuyên dùng)
Bạn cần cài đặt **Docker Desktop** trước khi bắt đầu.
1. Mở Terminal tại thư mục dự án.
2. Chạy lệnh:
   ```bash
   docker-compose up --build
   ```
3. Truy cập: `http://localhost:8080`

### Cách 2: Chạy trực tiếp trên máy
1. Tạo database MySQL có tên `web_dat_ban`.
2. Cấu hình username/password MySQL trong file `src/main/resources/application.properties`.
3. Chạy lệnh:
   ```bash
   ./mvnw spring-boot:run
   ```
4. Truy cập: `http://localhost:8080`

---

## 🔑 Tài Khoản Truy Cập (Mặc định)
Hệ thống tự động khởi tạo tài khoản quản trị khi chạy lần đầu:
*   **Username:** `admin`
*   **Password:** `123456`
*   **Vai trò:** Quản trị viên (Admin) & Nhân viên (Staff).

---

## 👤 Tác Giả
Dự án được hoàn thiện cho Đồ án Lập trình Web.
*   Nhóm: **Đạt - Nghiêm - Đại**

---
*Chúc bạn có trải nghiệm tuyệt vời cùng Lumina Restaurant!*
