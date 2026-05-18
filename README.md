# 🍽️ Hệ Thống Quản Lý Đặt Bàn & Thực Đơn Điện Tử (Lumina Restaurant)

Dự án phát triển bằng **Spring Boot 3** và **MySQL**, hỗ trợ quản lý đặt bàn trực tuyến, gọi món tại bàn, thanh toán hóa đơn và quản trị hệ thống nhà hàng.

---

## 🛠️ Yêu cầu hệ thống (Prerequisites)
Trước khi chạy dự án, hãy đảm bảo máy tính của bạn đã cài đặt:
1. **Java 17** trở lên.
2. **Docker & Docker Compose** (để chạy database MySQL nhanh chóng mà không cần cài đặt cục bộ).

---

## 🚀 Hướng Dẫn Chạy Dự Án Nhanh (Quick Start)

Hãy mở Terminal (hoặc Git Bash/Command Prompt) tại thư mục gốc của dự án này và chạy tuần tự các bước sau:

### Bước 1: Khởi động Database MySQL bằng Docker
Lệnh này sẽ tự động tải hình ảnh MySQL và khởi động máy chủ cơ sở dữ liệu ngầm ở cổng `3307`:
```bash
docker-compose up -d db
```

### Bước 2: Nạp dữ liệu mẫu Tiếng Việt chuẩn (Chỉ cần chạy 1 lần)
Để hệ thống có sẵn các Khu vực (Tầng 1, Tầng 2), Bàn ăn (Bàn 01 -> 10) và Thực đơn món ăn đa dạng có dấu tiếng Việt, hãy chạy lệnh import này:
```bash
docker exec -i restaurant_db mysql --default-character-set=utf8mb4 -uroot -proot web_dat_ban < seed.sql
```

### Bước 3: Khởi động Ứng dụng Web Spring Boot
Chạy server web ở cổng `8081`:
```bash
./mvnw spring-boot:run
```
*(Nếu bạn dùng Windows PowerShell, hãy gõ: `.\mvnw.cmd spring-boot:run`)*

---

## 🌐 Đường Dẫn Truy Cập (Access URLs)

Sau khi Server báo chạy thành công, hãy mở trình duyệt web (Chrome, Edge, Safari...) và truy cập:

* **Trang chủ Khách hàng (Đặt bàn & Xem Menu):** [http://localhost:8081](http://localhost:8081)
* **Trang Đăng nhập:** [http://localhost:8081/login](http://localhost:8081/login)

---

## 🔑 Tài Khoản Thử Nghiệm (Default Credentials)

Tất cả các tài khoản mẫu dưới đây đều sử dụng mật khẩu là: **`123456`**

| Vai trò (Role) | Tài khoản (Username) | Mật khẩu (Password) | Nhiệm vụ chính trong hệ thống |
| :--- | :--- | :--- | :--- |
| **ADMIN** | `admin` | `123456` | Quản lý món ăn, khu vực, nhân viên và xem báo cáo hóa đơn. |
| **STAFF** | `admin2` | `123456` | Nhân viên phục vụ: Quản lý bàn ăn, tạo order, checkout tính tiền tại quầy. |
| **CUSTOMER** | `vincent` | `123456` | Khách hàng: Xem thực đơn, đặt bàn trước trực tuyến. |

---

## 📚 Tài Liệu Hướng Dẫn Chi Tiết Cho Lập Trình Viên
Nếu bạn muốn tìm hiểu sâu về kiến trúc mã nguồn của dự án này, hãy truy cập vào thư mục `learn-project/` để đọc chuỗi bài viết hướng dẫn cực kỳ chi tiết từ tổng quan đến từng lớp xử lý:
* `learn-project/01_Kien_Truc_Va_Luong_Chay.md` (Tổng quan mô hình MVC)
* `learn-project/02_Tang_Database_Va_Entity.md` (Cấu trúc Database & ORM)
* `learn-project/03_Tang_Nghiep_Vu_Service_Repository.md` (Logic nghiệp vụ)
* `learn-project/04_Tang_Giao_Tiep_Controller_Va_Security.md` (Routing & Phân quyền)
* `learn-project/05_Tang_Giao_Dien_Thymeleaf.md` (Giao diện Thymeleaf động)
