package vn.edu.ptit.restaurant.entity.enums;

public enum OrderStatus {
    PENDING,     // Mới gọi, chờ phục vụ
    SERVING,     // Đang phục vụ
    COMPLETED,   // Đã thanh toán xong
    CANCELLED    // Đã hủy
}
