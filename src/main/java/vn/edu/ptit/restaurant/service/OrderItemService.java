package vn.edu.ptit.restaurant.service;

import vn.edu.ptit.restaurant.entity.OrderItem;
import java.util.List;

public interface OrderItemService {
    List<OrderItem> findByOrderId(Long orderId);
    void addMenuItemToOrder(Long orderId, Long menuItemId, Integer quantity, String note);
    void deleteOrderItem(Long orderItemId);
}
