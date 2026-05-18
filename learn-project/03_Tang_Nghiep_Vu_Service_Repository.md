# Bài 3: Tầng Nghiệp vụ (Repository & Service)

Nếu `Entity` là các nguyên liệu, thì `Repository` là thủ kho, còn `Service` là Bếp trưởng. Đây là nơi chứa não bộ thực sự của toàn bộ dự án. Khi gặp bất kỳ một logic tính toán tiền nong, cập nhật trạng thái hay bắt lỗi, bạn hãy tìm ngay vào tầng `Service`.

## 1. Tầng Repository (`src/main/java/.../repository/`)

Mở thử file `OrderRepository.java`, bạn sẽ thấy một điều rất "kỳ lạ": **Nó hoàn toàn rỗng, không có code logic nào!**
```java
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);
}
```

### Chuyện gì đang xảy ra ở đây?
Đây là sức mạnh kinh hoàng của `Spring Data JPA`. Bạn chỉ cần viết đúng ngữ pháp tiếng Anh: `findBy` + `ThuộcTính`.
*   Ví dụ: `findByStatus(OrderStatus status)`
*   JPA sẽ tự động dịch nó thành câu lệnh SQL: `SELECT * FROM orders WHERE status = ?` và lấy dữ liệu trả về cho bạn.
Bạn không phải viết code lấy connection, không phải mở/đóng kết nối database, không cần map các cột bằng tay!

## 2. Tầng Service (`src/main/java/.../service/`)

Tầng `Service` được chia làm 2 phần cực kỳ chặt chẽ (đây là Design Pattern cực kỳ phổ biến tên là *Interface - Implementation*):
1.  **Interface (Ví dụ: `OrderService.java`):** Bản hợp đồng quy định Bếp trưởng phải nấu được những món gì (Khai báo các hàm rỗng).
2.  **Implementation (Ví dụ: `OrderServiceImpl.java`):** Cách nấu cụ thể từng món. Code logic nằm ở đây.

### Giải phẫu một hàm trong `OrderServiceImpl.java`
Hãy xem hàm Checkout (Thanh toán) mà hệ thống đang chạy:

```java
@Override
@Transactional
public void checkout(Long orderId) {
    // 1. Nhờ Thủ Kho đi tìm Hóa Đơn trong DB
    Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
    
    // 2. Chuyển trạng thái hóa đơn thành Đã Xong
    order.setStatus(OrderStatus.COMPLETED);
    
    // 3. (Mới thêm) Tìm xem hóa đơn này có xuất phát từ Đặt Bàn không. 
    // Nếu có, thì đánh dấu Đặt Bàn là Đã Xong luôn!
    if (order.getReservation() != null) {
        Reservation res = order.getReservation();
        res.setStatus(ReservationStatus.COMPLETED);
        reservationRepository.save(res); // Nhờ Thủ kho lưu lại
    }
    
    // 4. Giải phóng bàn ăn cho khách khác vào ngồi
    DiningTable table = order.getTable();
    table.setStatus(TableStatus.AVAILABLE);
    diningTableRepository.save(table);
    
    // 5. Lưu hóa đơn
    orderRepository.save(order);
}
```

### Bí thuật `@Transactional`
Bạn có để ý chữ `@Transactional` nằm trên đầu hàm không? Đây là chiếc phao cứu sinh của lập trình viên.
Giả sử trong hàm Checkout trên, lệnh Số 3 chạy thành công, nhưng lệnh Số 4 bị sập mạng hoặc rớt server. 
Nếu không có `@Transactional`, DB của bạn sẽ bị rác: Đặt bàn thì đã xong, nhưng Bàn thì vẫn báo bận (không giải phóng được)! 
Có `@Transactional`, Spring Boot sẽ **Rollback (Hủy) toàn bộ thao tác** quay về y như cũ. Dữ liệu của bạn luôn an toàn 100%.

### Cách mapping (Nối dây)
Trong `OrderServiceImpl.java`, bạn sẽ thấy:
```java
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {
    private final OrderRepository orderRepository;
}
```
Làm sao `OrderServiceImpl` có thể lấy được cái thùng chứa `OrderRepository` để dùng? Chữ `@RequiredArgsConstructor` (của Lombok) sẽ tự động tạo một hàm tạo (constructor) đi kèm. Khái niệm này gọi là **Dependency Injection (Tiêm phụ thuộc)**. Thay vì khởi tạo bằng lệnh `new OrderRepository()`, Spring sẽ "tiêm" cái giỏ đựng repo đã được nạp sẵn DB vào đây cho bạn dùng trực tiếp.
