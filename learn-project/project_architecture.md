# Tổng quan Kiến trúc & Luồng vận hành Dự án Web Đặt Bàn (web_dat_ban)

Dự án này là một ứng dụng Web Quản lý Đặt bàn và Menu Điện tử dành cho nhà hàng. Nó được xây dựng dựa trên mô hình **MVC (Model-View-Controller)** sử dụng **Spring Boot (Java)** và giao diện **Thymeleaf**.

Dưới đây là chi tiết về cấu trúc thư mục, tác dụng của từng thành phần và cách luồng dữ liệu di chuyển trong hệ thống.

---

## 1. Công nghệ sử dụng (Tech Stack)
Dựa vào file `pom.xml`, dự án sử dụng:
* **Backend**: Java 17, Spring Boot 3 (Spring Web MVC).
* **Database**: MySQL, thao tác qua Spring Data JPA.
* **Bảo mật**: Spring Security (phân quyền Admin, Staff, Customer).
* **Giao diện**: Thymeleaf HTML template.
* **Tiện ích**: Lombok (giảm code getter/setter boilerplate), Apache POI (Xuất Excel), OpenPDF (Xuất PDF).
* **Triển khai**: Docker & Docker Compose.

---

## 2. Cấu trúc thư mục (Project Structure)

Toàn bộ code backend nằm trong thư mục `src/main/java/vn/edu/ptit/restaurant`.
Giao diện và cấu hình nằm trong `src/main/resources`.

### 2.1. Tầng Entity (`/entity`) - Model
Chứa các lớp Java (Class) ánh xạ trực tiếp với các bảng trong cơ sở dữ liệu MySQL thông qua JPA (Hibernate).
* `User.java`: Bảng người dùng (Admin, Staff, Customer).
* `Area.java` & `DiningTable.java`: Bảng khu vực và bàn ăn.
* `Category.java` & `MenuItem.java`: Bảng danh mục và món ăn.
* `Reservation.java`: Bảng đặt bàn trước.
* `Order.java` & `OrderItem.java`: Bảng hóa đơn và chi tiết món ăn trong hóa đơn.
* `enums/`: Chứa các trạng thái cố định như `OrderStatus` (PENDING, PAID...), `Role` (ADMIN, STAFF, CUSTOMER).

### 2.2. Tầng Repository (`/repository`)
Đóng vai trò giao tiếp trực tiếp với cơ sở dữ liệu.
* Chỉ chứa các Interface (ví dụ: `UserRepository`, `OrderRepository`) kế thừa từ `JpaRepository`.
* Spring Boot sẽ tự động sinh code thực thi các câu lệnh SQL (CRUD) mà không cần viết thủ công.

### 2.3. Tầng Service (`/service` & `/service/impl`)
Chứa toàn bộ **logic nghiệp vụ (Business Logic)** của hệ thống.
* `/service`: Chứa các Interface định nghĩa các chức năng (vd: `OrderService.java`).
* `/service/impl`: Chứa các class triển khai (vd: `OrderServiceImpl.java`).
* Các class đặc biệt: `CartService` (quản lý giỏ hàng), `ExcelExportService` & `PdfExportService` (logic xuất báo cáo thống kê).

### 2.4. Tầng Controller (`/controller`)
Điểm tiếp nhận các HTTP Request từ trình duyệt (người dùng). Controller sẽ gọi Service để xử lý và trả về giao diện (View).
* `AuthController`: Xử lý Đăng nhập, Đăng ký.
* `CustomerController`: Xử lý luồng khách hàng (Xem menu, thêm giỏ hàng, đặt bàn).
* `StaffOrderController`: Giao diện của nhân viên (Quản lý hóa đơn tại quán, gọi món cho khách).
* `Admin...Controller` (`AdminMenuController`, `AdminTableController`, `AdminReportController`): Quản lý dữ liệu hệ thống và xem báo cáo.

### 2.5. Tầng DTO (`/dto`)
**Data Transfer Object**: Đối tượng vận chuyển dữ liệu.
* `CartItem`, `ReportDTO`: Các object này không lưu vào DB, chỉ dùng để gom nhóm dữ liệu truyền từ Backend ra Giao diện một cách dễ dàng (ví dụ gom số liệu để vẽ biểu đồ/báo cáo).

### 2.6. Tầng Security (`/security`)
Xử lý bảo mật và phân quyền hệ thống.
* `SecurityConfig.java`: Khai báo các đường dẫn nào cần quyền gì (ví dụ URL bắt đầu bằng `/admin/**` thì tài khoản phải có role ADMIN).
* `CustomUserDetailsService`: Logic tìm kiếm người dùng trong DB khi họ gõ username/password.
* `CustomAuthenticationSuccessHandler`: Logic điều hướng sau khi login thành công (Admin -> trang dashboard admin, Customer -> trang chủ, Staff -> trang quản lý bàn).

### 2.7. Tầng Giao diện (`src/main/resources`)
* `application.properties` / `application-docker.properties`: Cấu hình kết nối Database, cổng chạy web (port).
* `/templates`: Chứa các file HTML Thymeleaf, chia theo thư mục phân quyền:
  * `/admin`: Giao diện quản trị, xem biểu đồ, quản lý menu/bàn.
  * `/staff`: Giao diện cho nhân viên nhận đơn, thanh toán.
  * `/customer`: Giao diện khách hàng (giỏ hàng, đặt bàn).
  * `login.html`, `register.html`, `index.html`: Các trang chung.

---

## 3. Luồng vận hành hệ thống (Workflow)

Để hiểu rõ hơn, hãy đi theo một kịch bản **Khách hàng vào xem Menu và Đặt bàn**:

> [!NOTE]
> **Bước 1: Trình duyệt gửi Request**  
> Khách hàng mở web và truy cập vào `/customer/menu`.

> [!NOTE]
> **Bước 2: Security Filter (Bảo vệ)**  
> Spring Security (`SecurityConfig`) kiểm tra xem khách hàng đã đăng nhập chưa. Nếu chưa đăng nhập, hệ thống đẩy về trang `login.html`. Nếu đã đăng nhập với Role CUSTOMER, cho phép đi tiếp.

> [!NOTE]
> **Bước 3: Controller tiếp nhận**  
> `CustomerController` nhận request vào hàm map với URL `/customer/menu`. Controller cần lấy danh sách món ăn để hiển thị.

> [!NOTE]
> **Bước 4: Controller gọi Service**  
> `CustomerController` gọi phương thức `menuItemService.findAll()` (Logic nghiệp vụ).

> [!NOTE]
> **Bước 5: Service gọi Repository**  
> `MenuItemServiceImpl` gọi `menuItemRepository.findAll()` để thực thi câu lệnh SQL: `SELECT * FROM menu_items`.

> [!NOTE]
> **Bước 6: Trả về Entity**  
> `Repository` kết nối tới MySQL, lấy data, và gói vào các object `MenuItem` (thuộc thư mục Entity) rồi trả ngược lại cho `Service`, sau đó trả ra `Controller`.

> [!NOTE]
> **Bước 7: Controller trả về View (Thymeleaf)**  
> `CustomerController` gắn danh sách `MenuItem` vào `Model` và chỉ định trả về file giao diện `customer/menu.html`. Thymeleaf sẽ render HTML động dựa trên danh sách món ăn này và gửi về lại cho trình duyệt hiển thị.

## 4. Tóm lược

Mô hình này đảm bảo tính đóng gói cực kỳ cao:
- Muốn sửa giao diện -> Vào `resources/templates/`
- Muốn thêm thuộc tính (ví dụ thêm ảnh cho món ăn) -> Sửa `Entity` -> Sửa DB.
- Muốn thay đổi logic tính tiền -> Sửa ở `Service`.
- Muốn đổi đường dẫn URL -> Sửa ở `Controller`.
