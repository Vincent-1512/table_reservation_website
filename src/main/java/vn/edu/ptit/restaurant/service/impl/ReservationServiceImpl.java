package vn.edu.ptit.restaurant.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.ptit.restaurant.dto.CartItem;
import vn.edu.ptit.restaurant.entity.*;
import vn.edu.ptit.restaurant.entity.enums.OrderStatus;
import vn.edu.ptit.restaurant.entity.enums.ReservationStatus;
import vn.edu.ptit.restaurant.entity.enums.TableStatus;
import vn.edu.ptit.restaurant.repository.*;
import vn.edu.ptit.restaurant.service.CartService;
import vn.edu.ptit.restaurant.service.ReservationService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final DiningTableRepository diningTableRepository;
    private final UserRepository userRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final MenuItemRepository menuItemRepository;
    private final CartService cartService;

    @Override
    @Transactional
    public Reservation createReservation(Long userId, Long tableId, LocalDateTime reservationTime, Integer numberOfGuests, String note) {
        User user = userRepository.findById(userId).orElseThrow();
        DiningTable table = diningTableRepository.findById(tableId).orElseThrow();

        // Kiểm tra kẹt bàn (Optimistic Locking sẽ hoạt động ngầm nhờ @Version)
        // Nếu có 2 transaction cùng cố gắng cập nhật 1 bàn, JPA sẽ ném ra ObjectOptimisticLockingFailureException
        if (table.getStatus() != TableStatus.AVAILABLE) {
            throw new RuntimeException("Bàn này không còn trống!");
        }

        // Đặt bàn thành RESERVED
        table.setStatus(TableStatus.RESERVED);
        diningTableRepository.save(table);

        Reservation reservation = Reservation.builder()
                .user(user)
                .table(table)
                .reservationTime(reservationTime)
                .numberOfGuests(numberOfGuests)
                .note(note)
                .status(ReservationStatus.PENDING)
                .build();
        
        Reservation savedReservation = reservationRepository.save(reservation);

        // Nếu khách có đặt món trước (có giỏ hàng) -> Tạo luôn Order (PENDING)
        if (cartService.getCount() > 0) {
            Order order = Order.builder()
                    .table(table)
                    .user(user)
                    .reservation(savedReservation)
                    .totalAmount(cartService.getAmount())
                    .status(OrderStatus.PENDING)
                    .build();
            Order savedOrder = orderRepository.save(order);

            for (CartItem item : cartService.getItems()) {
                MenuItem menuItem = menuItemRepository.findById(item.getMenuItemId()).orElseThrow();
                OrderItem orderItem = OrderItem.builder()
                        .order(savedOrder)
                        .menuItem(menuItem)
                        .quantity(item.getQuantity())
                        .priceAtTime(item.getPrice())
                        .build();
                orderItemRepository.save(orderItem);
            }
            
            // Xóa giỏ hàng sau khi đặt bàn thành công
            cartService.clear();
        }

        return savedReservation;
    }

    @Override
    public List<Reservation> findByUserId(Long userId) {
        return reservationRepository.findByUserIdOrderByReservationTimeDesc(userId);
    }

    @Override
    public List<Reservation> findByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status);
    }
    
    @Override
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }

    @Override
    @Transactional
    public void cancelReservation(Long reservationId, Long userId) {
        Reservation res = reservationRepository.findById(reservationId).orElseThrow();
        if (!res.getUser().getId().equals(userId)) {
            throw new RuntimeException("Không có quyền hủy đặt bàn này!");
        }
        res.setStatus(ReservationStatus.CANCELLED);
        
        // Trả lại bàn
        DiningTable table = res.getTable();
        table.setStatus(TableStatus.AVAILABLE);
        diningTableRepository.save(table);
        
        reservationRepository.save(res);
    }
}
