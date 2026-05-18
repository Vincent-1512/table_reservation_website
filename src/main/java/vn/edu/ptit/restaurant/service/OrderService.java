package vn.edu.ptit.restaurant.service;

import vn.edu.ptit.restaurant.entity.Order;
import vn.edu.ptit.restaurant.entity.enums.OrderStatus;
import org.springframework.data.domain.Page;
import java.time.LocalDate;

import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<Order> findAll();
    List<Order> findByStatus(OrderStatus status);
    Optional<Order> findById(Long id);
    Order createOrder(Long tableId, String username);
    Order save(Order order);
    Page<Order> findPaginated(int page, int size);

    Page<Order> filterOrders(
        OrderStatus status,
        LocalDate startDate,
        LocalDate endDate,
        int page,   
        int size
    );

    Page<Order> findByStatusPaginated(OrderStatus status, int page, int size);
    void updateOrderStatus(Long orderId, OrderStatus newStatus);
    void checkout(Long orderId);
}
