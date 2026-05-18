# Bài 4: Tầng Giao tiếp (Controller & Security)

Tầng này giống như bộ phận Tiếp tân của dự án. Khách đến gõ cửa qua URL, Tiếp tân sẽ đón tiếp, kiểm tra giấy tờ tùy thân (bảo vệ bằng Security), và hướng dẫn khách đến các phòng chức năng.

## 1. Controller - Cửa ngõ giao tiếp (`src/main/java/.../controller/`)

Dự án đang chia các Controller làm 3 nhóm chính tương ứng với 3 vai trò (Role):
- **`admin`**: Quản lý toàn bộ hệ thống (Khu vực, Món ăn, Doanh thu...).
- **`staff`**: Nhân viên (Quản lý Bàn và Hóa đơn hiện tại).
- **`customer`**: Khách hàng vãng lai (Xem Menu, Bấm Đặt bàn).

### Giải phẫu một hàm Controller (VD: `AdminOrderController.java`)
Mỗi hàm trong Controller đều được gắn một `Mapping` (Đường dẫn).

```java
@Controller // Đánh dấu đây là file Tiếp tân
@RequestMapping("/admin/orders") // Tiền tố chung: Bất cứ ai truy cập /admin/orders đều bay vào đây
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService; // Nhờ Bếp trưởng (Service) xử lý data

    // Khi gõ trình duyệt URL: GET /admin/orders
    @GetMapping 
    public String index(Model model) {
        List<Order> orders = orderService.findAll(); // Xin dữ liệu
        model.addAttribute("orders", orders);        // Bỏ vào túi nilon Model mang tên 'orders'
        return "admin/order/index";                  // Chỉ đường cho Spring Boot mở file giao diện `admin/order/index.html`
    }
}
```

### Cách truyền dữ liệu qua lại (Gửi Form)
Khi Admin bấm nút "Thanh toán", file HTML sẽ bắn dữ liệu bằng hàm `POST`.
```java
@PostMapping("/{id}/checkout")
public String checkoutOrder(@PathVariable Long id) { // @PathVariable hút cái chữ {id} trên URL (ví dụ /admin/orders/5/checkout thì id = 5)
    orderService.checkout(id);
    return "redirect:/admin/orders"; // Làm xong thì đá (redirect) người dùng về lại trang danh sách ban đầu
}
```

---

## 2. Security - Trạm gác bảo vệ (`src/main/java/.../security/`)

Nếu không có trạm gác này, bất cứ ai biết URL `/admin/orders` cũng có thể chui vào hệ thống quản lý nhà hàng! Chúng ta sử dụng **Spring Security**.

Mở file `SecurityConfig.java`:

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        // ... (các cấu hình chặn/mở)
        .authorizeHttpRequests(auth -> auth
            // NHỮNG ĐƯỜNG DẪN MỞ CỬA TỰ DO (Chưa đăng nhập vẫn xem được)
            .requestMatchers("/", "/menu", "/cart/**", "/reservation/**", "/login", "/register").permitAll()
            
            // CHỈ ADMIN MỚI ĐƯỢC CHUI VÀO CÁC ĐƯỜNG DẪN /admin/...
            .requestMatchers("/admin/**").hasAuthority("ADMIN")
            
            // CHỈ STAFF HOẶC ADMIN MỚI ĐƯỢC VÀO /staff/...
            .requestMatchers("/staff/**").hasAnyAuthority("STAFF", "ADMIN")
            
            // Bất kỳ đường dẫn nào khác đều phải Đăng nhập
            .anyRequest().authenticated()
        )
        // CẤU HÌNH TRANG ĐĂNG NHẬP
        .formLogin(form -> form
            .loginPage("/login") // File giao diện login.html
            .successHandler(customAuthenticationSuccessHandler) // Đăng nhập xong thì đẩy đi đâu? (Admin đẩy vào dashboard, Customer đẩy vào trang chủ)
        )
        // ...
    return http.build();
}
```

### Bảo vệ kiểm tra giấy tờ thế nào?
Spring Security cần một người đi tra sổ thông tin tài khoản xem Khách đó có nhập đúng mật khẩu không. Người đó là file `CustomUserDetailsService.java`:
- Khi bạn nhập chữ `vincent` trên form Login.
- File này sẽ gọi Thủ Kho (`UserRepository`) hỏi xem có ông `vincent` dưới Database không.
- Nếu có, nó lấy `password_hash` dưới DB lên. Spring Security ngầm lấy mật khẩu bạn gõ (`123456`), mã hóa ra rồi tự so sánh với đoạn mã trong DB. Nếu khớp -> Cấp quyền!
