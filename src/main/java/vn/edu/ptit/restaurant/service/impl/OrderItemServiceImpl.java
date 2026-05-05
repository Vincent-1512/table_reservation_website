package vn.edu.ptit.restaurant.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.ptit.restaurant.entity.MenuItem;
import vn.edu.ptit.restaurant.entity.Order;
import vn.edu.ptit.restaurant.entity.OrderItem;
import vn.edu.ptit.restaurant.repository.MenuItemRepository;
import vn.edu.ptit.restaurant.repository.OrderItemRepository;
import vn.edu.ptit.restaurant.repository.OrderRepository;
import vn.edu.ptit.restaurant.service.OrderItemService;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderItemServiceImpl implements OrderItemService {

    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;
    private final MenuItemRepository menuItemRepository;

    @Override
    public List<OrderItem> findByOrderId(Long orderId) {
        return orderItemRepository.findByOrderId(orderId);
    }

    @Override
    @Transactional
    public void addMenuItemToOrder(Long orderId, Long menuItemId, Integer quantity, String note) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));
        MenuItem menuItem = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món ăn"));

        OrderItem orderItem = OrderItem.builder()
                .order(order)
                .menuItem(menuItem)
                .quantity(quantity)
                .priceAtTime(menuItem.getPrice()) // Lưu giá tại thời điểm gọi món
                .note(note)
                .build();
        
        orderItemRepository.save(orderItem);

        // Cập nhật tổng tiền hóa đơn
        BigDecimal additionalAmount = menuItem.getPrice().multiply(new BigDecimal(quantity));
        order.setTotalAmount(order.getTotalAmount().add(additionalAmount));
        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void deleteOrderItem(Long orderItemId) {
        OrderItem item = orderItemRepository.findById(orderItemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy món trong hóa đơn"));
        
        Order order = item.getOrder();
        BigDecimal deductedAmount = item.getPriceAtTime().multiply(new BigDecimal(item.getQuantity()));
        order.setTotalAmount(order.getTotalAmount().subtract(deductedAmount));
        
        orderItemRepository.delete(item);
        orderRepository.save(order);
    }
}
