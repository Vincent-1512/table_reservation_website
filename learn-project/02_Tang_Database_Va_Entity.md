# Bài 2: Tầng Database và Entity (Lõi Dữ Liệu)

Tầng `Entity` nằm trong thư mục `src/main/java/vn/edu/ptit/restaurant/entity`. 
Đây là trái tim của dự án, mọi dữ liệu lưu trữ, truy xuất, thao tác đều xoay quanh các file này. Nếu bạn muốn biết hệ thống này quản lý cái gì, chỉ cần nhìn vào thư mục này là rõ.

## 1. Ý nghĩa của "Entity" trong JPA/Hibernate
Trong dự án này, chúng ta KHÔNG viết câu lệnh SQL kiểu `CREATE TABLE users...` để tạo bảng. Chúng ta sử dụng một công nghệ gọi là **JPA/Hibernate (ORM)**. 
Công nghệ này cho phép bạn viết các Class bằng Java, và hệ thống sẽ **tự động dịch (mapping)** các Class đó thành các bảng trong MySQL.

**Quy tắc:**
- 1 Class (Ví dụ: `User.java`) = 1 Bảng trong DB (Bảng `users`).
- 1 Thuộc tính (Ví dụ: `private String username;`) = 1 Cột trong DB (Cột `username`).
- 1 Object của Class đó = 1 Dòng (Record) trong Database.

## 2. Giải phẫu một file Entity điển hình (Ví dụ: `User.java`)
Hãy mở file `User.java` ra, bạn sẽ thấy nó chằng chịt các ký tự `@` (gọi là Annotation). Dưới đây là ý nghĩa của chúng:

```java
@Entity 
@Table(name = "users") 
```
*   `@Entity`: Báo cho Spring Boot biết Class này không phải class bình thường, mà là một Entity dùng để tạo bảng.
*   `@Table(name = "users")`: Quy định tên bảng trong MySQL sẽ là `users` (chữ thường, số nhiều). Nếu không có, nó sẽ lấy đúng tên Class là `User`.

```java
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
```
Đây là **Lombok**. Thay vì bạn phải tự tay viết các hàm `getUsername()`, `setUsername()`, hàm khởi tạo... thì 4 cái thẻ này tự sinh ra toàn bộ các hàm đó ngầm bên dưới lúc chạy. Code của bạn sẽ cực kỳ ngắn gọn.

```java
@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Long id;
```
*   `@Id`: Đánh dấu cột này là Khóa Chính (Primary Key).
*   `@GeneratedValue(strategy = GenerationType.IDENTITY)`: Báo cho MySQL tự động tăng số thứ tự (Auto Increment). Bạn không cần truyền ID khi tạo mới User.

## 3. Bản đồ các file trong hệ thống (ERD Mapping)
Dự án của bạn có 9 Entities chính. Chúng liên kết với nhau bằng các "sợi dây" quan hệ.

### 👥 Nhóm Con người & Phân quyền
1.  **`User.java`**: Chứa thông tin tài khoản (Admin, Staff, Customer). Cột `password_hash` lưu mật khẩu đã được mã hóa (bảo mật). Cột `role` dùng Enum để định danh quyền hạn.

### 🍽️ Nhóm Không gian & Bàn ăn
2.  **`Area.java`**: Khu vực (Tầng 1, Tầng 2, Sân thượng).
3.  **`DiningTable.java`**: Bàn ăn.
    *   *Mối quan hệ:* 1 Khu vực có nhiều Bàn ăn (`@OneToMany` trong Area, và `@ManyToOne` trong DiningTable).
    *   *Kỹ thuật cực hay:* Trong `DiningTable` có thuộc tính `@Version private Long version;`. Đây là kỹ thuật **Optimistic Locking** giúp chống "kẹt bàn". (Nếu 2 nhân viên cùng lúc bấm chọn bàn số 1, người thứ 2 sẽ bị báo lỗi vì version đã thay đổi).

### 🍔 Nhóm Thực đơn
4.  **`Category.java`**: Danh mục (Khai vị, Món chính...).
5.  **`MenuItem.java`**: Món ăn chi tiết. 
    *   *Mối quan hệ:* Nhiều món ăn thuộc về 1 danh mục (`@ManyToOne` trỏ về Category).

### 📝 Nhóm Giao dịch (Core Business)
Đây là nhóm phức tạp nhất, diễn tả luồng kinh doanh của nhà hàng.
6.  **`Reservation.java`**: Phiếu đặt bàn trước của khách. (Liên kết với Khách hàng `User` và Bàn `DiningTable`).
7.  **`Order.java`**: Hóa đơn/Phiếu gọi món. 
    *   Khi khách ngồi vào bàn (Dù là vãng lai hay đã đặt trước), một `Order` sẽ được sinh ra. 
    *   Nó có quan hệ `@OneToOne` với `Reservation` (1 Phiếu đặt bàn nếu thành công sẽ sinh ra đúng 1 Hóa đơn).
8.  **`OrderItem.java`**: Chi tiết hóa đơn (Khách gọi những món gì, số lượng bao nhiêu).
    *   *Mối quan hệ:* 1 `Order` chứa nhiều `OrderItem`.
9.  **`Payment.java`**: Lịch sử thanh toán.
    *   Khi `Order` hoàn tất, một `Payment` được sinh ra để lưu vết số tiền và hình thức thanh toán (Tiền mặt, Chuyển khoản).

---
> [!TIP]
> **Thư mục `enums` là gì?**
> Nằm trong `entity/enums`, nó chứa các hằng số cố định. Thay vì lưu trạng thái bàn là String "Trống", "Có Khách" (dễ gõ sai chính tả), chúng ta dùng thẻ Enum `AVAILABLE`, `OCCUPIED`. Khi lưu vào MySQL nó vẫn là chữ, nhưng lúc code ở Java thì gõ `TableStatus.` là nó hiện ra gợi ý, không bao giờ bị sai!
