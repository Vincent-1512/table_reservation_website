# Sơ đồ Luồng chạy thực tế (Execution Flows)

Để dễ hình dung cách các file "nói chuyện" với nhau, hãy cùng xem các sơ đồ luồng (Sequence Diagram) dưới đây. Mỗi mũi tên thể hiện một bước dữ liệu truyền qua lại giữa các thành phần.

## 1. Luồng chạy thực tế: Quá trình Đăng nhập (Login)

Quá trình này giải thích cách Spring Security và `UserRepository` hoạt động cùng nhau khi người dùng gõ Username/Password.

```mermaid
sequenceDiagram
    autonumber
    actor User as Khách hàng
    participant Browser as Trình duyệt (View)
    participant Sec as SecurityConfig (Filter)
    participant Auth as CustomUserDetailsService
    participant Repo as UserRepository
    participant DB as MySQL Database

    User->>Browser: Gõ Username & Password
    Browser->>Sec: Gửi POST /login (form data)
    Sec->>Auth: Yêu cầu xác thực username
    Auth->>Repo: Gọi phương thức findByUsername(username)
    Repo->>DB: Thực thi: SELECT * FROM user WHERE username=?
    
    alt Không tìm thấy User
        DB-->>Repo: Trả về null
        Repo-->>Auth: Trả về Optional.empty()
        Auth-->>Sec: Ném lỗi UsernameNotFoundException
        Sec-->>Browser: Redirect về /login?error
        Browser-->>User: Hiện thông báo "Sai tài khoản/mật khẩu"
    else Tìm thấy User
        DB-->>Repo: Trả về dữ liệu User
        Repo-->>Auth: Trả về User Entity
        Auth->>Auth: Kiểm tra Password có khớp mã hóa (BCrypt) không?
        
        alt Sai mật khẩu
            Auth-->>Sec: Báo lỗi sai mật khẩu
            Sec-->>Browser: Redirect về /login?error
            Browser-->>User: Hiện thông báo lỗi
        else Đúng mật khẩu
            Auth-->>Sec: Trả về đối tượng UserDetails (Hợp lệ)
            Sec->>Sec: Lưu thông tin User vào Session (Đã đăng nhập)
            Sec-->>Browser: Điều hướng (Admin -> /admin, Khách -> /)
            Browser-->>User: Hiển thị giao diện trang chủ/quản trị
        end
    end
```

---

## 2. Luồng chạy thực tế: Khách hàng xem Danh sách Bàn ăn

Ví dụ này sẽ chỉ ra vai trò của `Controller`, `Service`, `Repository` và `View` khi bạn muốn hiển thị một danh sách lên màn hình.

```mermaid
sequenceDiagram
    autonumber
    actor User as Khách hàng
    participant Browser as Trình duyệt
    participant Controller as CustomerController
    participant Service as DiningTableService
    participant Repo as DiningTableRepository
    participant DB as MySQL Database
    participant View as Thymeleaf (customer/reservation.html)

    User->>Browser: Click vào "Đặt bàn"
    Browser->>Controller: GET request đến URL /customer/reservation
    Controller->>Service: Gọi hàm findAll() để lấy list bàn
    Service->>Repo: Gọi hàm findAll()
    Repo->>DB: Chạy SQL: SELECT * FROM dining_table
    DB-->>Repo: Trả về dữ liệu các dòng bảng dining_table
    Repo-->>Service: Trả về List<DiningTable> (Entity)
    Service-->>Controller: Trả về List<DiningTable>
    Controller->>Controller: Đính kèm List này vào đối tượng Model: model.addAttribute("tables", list)
    Controller->>View: Báo Thymeleaf: "Hãy render file reservation.html với đống data này"
    View->>View: Dùng vòng lặp (th:each="table : ${tables}") để tạo mã HTML cho từng bàn
    View-->>Browser: Trả về mã HTML thuần (Giao diện đã có dữ liệu)
    Browser-->>User: Nhìn thấy danh sách các bàn trên màn hình
```

---

## 3. Tóm tắt "Luật lệ" truyền dữ liệu

Để hệ thống không bị "rối", Spring Boot ép chúng ta tuân thủ quy tắc truyền dữ liệu theo đúng **1 chiều**:

1. **Browser** KHÔNG ĐƯỢC gọi thẳng xuống **Database**.
2. **Controller** KHÔNG ĐƯỢC gọi thẳng **Repository**. (Nó phải nhờ **Service** làm trung gian).
3. **Repository** chỉ làm một nhiệm vụ duy nhất: Giao tiếp với **Database**. Không được chứa logic tính toán (như tính thuế, giảm giá...). Logic tính toán phải nằm ở **Service**.

Quy trình chuẩn luôn luôn là:  
`Trình duyệt` ⇄ `Controller` ⇄ `Service` ⇄ `Repository` ⇄ `Database`
