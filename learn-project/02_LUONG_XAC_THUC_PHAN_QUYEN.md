# 🔐 LUỒNG XÁC THỰC & PHÂN QUYỀN

## 1. Tổng quan Spring Security trong dự án

Spring Security bảo vệ toàn bộ ứng dụng thông qua 3 file chính:

| File | Chức năng |
|------|-----------|
| `SecurityConfig.java` | Cấu hình URL nào ai được truy cập, form đăng nhập, logout |
| `CustomUserDetailsService.java` | Tìm user trong DB khi đăng nhập |
| `CustomAuthenticationSuccessHandler.java` | Sau khi login thành công → chuyển hướng theo role |

---

## 2. Luồng đăng nhập chi tiết

```mermaid
sequenceDiagram
    actor User as 👤 Người dùng
    participant Browser as 🌐 Trình duyệt
    participant Filter as 🔒 Spring Security Filter
    participant AuthCtrl as 📋 AuthController
    participant UDS as 👤 CustomUserDetailsService
    participant DB as 💾 Database
    participant Handler as 🔀 SuccessHandler

    User->>Browser: Truy cập /login
    Browser->>AuthCtrl: GET /login
    AuthCtrl-->>Browser: Trả về trang login.html

    User->>Browser: Nhập username + password → Bấm "Đăng nhập"
    Browser->>Filter: POST /login (username, password)

    Note over Filter: Spring Security tự<br/>bắt POST /login

    Filter->>UDS: loadUserByUsername("admin")
    UDS->>DB: SELECT * FROM users WHERE username = 'admin'
    DB-->>UDS: Trả về User (id, username, password_hash, role)

    UDS-->>Filter: Trả UserDetails (username, encodedPwd, ROLE_ADMIN)

    Note over Filter: So sánh password nhập vào<br/>với password_hash trong DB<br/>bằng BCryptEncoder

    alt ✅ Mật khẩu ĐÚNG
        Filter->>Filter: Tạo Authentication + HttpSession
        Filter->>Handler: Gọi onAuthenticationSuccess()
        
        alt Role = ADMIN
            Handler-->>Browser: redirect → /admin/reports
        else Role = STAFF
            Handler-->>Browser: redirect → /staff/orders
        else Role = CUSTOMER
            Handler-->>Browser: redirect → /
        end
    else ❌ Mật khẩu SAI
        Filter-->>Browser: redirect → /login?error
        Browser-->>User: Hiển thị "Sai tên đăng nhập hoặc mật khẩu"
    end
```

### Giải thích từng bước:

| Bước | Mô tả | Code tương ứng |
|------|-------|----------------|
| 1 | Người dùng mở trang `/login` | `AuthController.showLoginForm()` → trả `login.html` |
| 2 | Nhập username + password rồi bấm "Đăng nhập" | Form submit POST `/login` |
| 3 | Spring Security tự động bắt request POST `/login` | Cấu hình trong `SecurityConfig.formLogin()` |
| 4 | Gọi `loadUserByUsername()` để tìm user trong DB | `CustomUserDetailsService` → `UserRepository.findByUsername()` |
| 5 | So sánh password đã mã hóa | `BCryptPasswordEncoder.matches(rawPwd, encodedPwd)` |
| 6 | Nếu đúng → tạo Session → gọi SuccessHandler | `CustomAuthenticationSuccessHandler.onAuthenticationSuccess()` |
| 7 | SuccessHandler kiểm tra role → redirect phù hợp | `ROLE_ADMIN` → `/admin/reports`, `ROLE_STAFF` → `/staff/orders` |

---

## 3. Cấu hình phân quyền URL

```mermaid
graph LR
    subgraph "🟢 PUBLIC (Ai cũng vào được)"
        A["/ (Trang chủ)"]
        B["/login"]
        C["/register"]
        D["/css/**, /js/**, /images/**"]
    end

    subgraph "🔵 AUTHENTICATED (Phải đăng nhập)"
        E["/menu"]
        F["/cart/**"]
        G["/reservation"]
        H["/my-reservations"]
    end

    subgraph "🟡 ROLE_STAFF hoặc ROLE_ADMIN"
        I["/staff/**"]
    end

    subgraph "🔴 CHỈ ROLE_ADMIN"
        J["/admin/**"]
    end
```

**Code thực tế trong `SecurityConfig.java`:**

```java
http.authorizeHttpRequests(authorize -> authorize
    // ✅ Công khai - ai cũng vào được
    .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/images/**")
        .permitAll()

    // ✅ Phải đăng nhập mới vào được
    .requestMatchers("/menu", "/cart/**", "/reservation", "/my-reservations")
        .authenticated()

    // ✅ Chỉ ADMIN mới vào
    .requestMatchers("/admin/**").hasRole("ADMIN")

    // ✅ ADMIN hoặc STAFF đều vào được
    .requestMatchers("/staff/**").hasAnyRole("ADMIN", "STAFF")

    // ✅ Mọi URL còn lại → phải đăng nhập
    .anyRequest().authenticated()
);
```

---

## 4. Luồng đăng ký tài khoản

```mermaid
sequenceDiagram
    actor User as 👤 Khách
    participant Browser as 🌐 Trình duyệt
    participant AuthCtrl as 📋 AuthController
    participant UserSrv as ⚙️ UserService
    participant DB as 💾 Database

    User->>Browser: Truy cập /register
    Browser->>AuthCtrl: GET /register
    AuthCtrl-->>Browser: Trả về register.html

    User->>Browser: Nhập thông tin → Bấm "Đăng ký"
    Browser->>AuthCtrl: POST /register (user data)

    AuthCtrl->>UserSrv: findByUsername(username)
    UserSrv->>DB: SELECT * FROM users WHERE username = ?

    alt Username đã tồn tại
        DB-->>UserSrv: Tìm thấy user
        UserSrv-->>AuthCtrl: return Optional.of(user)
        AuthCtrl-->>Browser: Trả register.html + lỗi "Tên đăng nhập đã tồn tại!"
    else Username chưa có
        DB-->>UserSrv: Không tìm thấy
        UserSrv-->>AuthCtrl: return Optional.empty()
        AuthCtrl->>UserSrv: registerNewUserAccount(user)
        Note over UserSrv: Mã hóa password bằng BCrypt<br/>Gán role = CUSTOMER<br/>Lưu vào DB
        UserSrv->>DB: INSERT INTO users (...)
        AuthCtrl-->>Browser: redirect → /login?registered=true
        Browser-->>User: Hiển thị "Đăng ký thành công!"
    end
```

---

## 5. Luồng Logout

```mermaid
sequenceDiagram
    actor User as 👤 Người dùng
    participant Browser as 🌐 Trình duyệt
    participant Security as 🔒 Spring Security

    User->>Browser: Bấm nút "Đăng xuất"
    Browser->>Security: POST /logout
    Security->>Security: Hủy HttpSession (xóa thông tin đăng nhập)
    Security-->>Browser: redirect → / (Trang chủ)
    Browser-->>User: Hiển thị trang chủ (chưa đăng nhập)
```

---

## 6. Tóm tắt luồng xác thực

```mermaid
flowchart TD
    A[Người dùng truy cập URL] --> B{URL có cần<br/>đăng nhập không?}
    
    B -->|Không: /, /login, /register| C[✅ Truy cập trực tiếp]
    B -->|Có| D{Đã đăng nhập chưa?}
    
    D -->|Chưa| E[🔒 Redirect → /login]
    D -->|Rồi| F{Có đủ quyền không?}
    
    F -->|Không đủ quyền| G[⛔ 403 Forbidden]
    F -->|Đủ quyền| H[✅ Controller xử lý → Trả View]

    E --> I[Nhập username/password]
    I --> J{Đúng?}
    J -->|Sai| K[❌ Quay lại /login + Báo lỗi]
    J -->|Đúng| L[SuccessHandler kiểm tra Role]
    
    L -->|ADMIN| M["→ /admin/reports"]
    L -->|STAFF| N["→ /staff/orders"]
    L -->|CUSTOMER| O["→ / (Trang chủ)"]
```
