package vn.edu.ptit.restaurant.service;

import vn.edu.ptit.restaurant.entity.Order;
import vn.edu.ptit.restaurant.entity.enums.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    List<Order> findAll();
    List<Order> findByStatus(OrderStatus status);
    Optional<Order> findById(Long id);
    Order createOrder(Long tableId, String username);
    Order save(Order order);
    void updateOrderStatus(Long orderId, OrderStatus newStatus);
    void checkout(Long orderId);
}
