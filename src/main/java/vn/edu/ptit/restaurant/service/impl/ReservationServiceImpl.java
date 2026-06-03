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
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;

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
        java.time.LocalDateTime startOfToday = java.time.LocalDate.now().atStartOfDay();
        return reservationRepository.findByUserIdAndReservationTimeGreaterThanEqualOrderByReservationTimeDesc(userId, startOfToday);
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
    public Page<Reservation> searchReservations(String keyword,
                                                ReservationStatus status,
                                                LocalDate startDate,
                                                LocalDate endDate,
                                                int page,
                                                int size) {
        Pageable pageable = PageRequest.of(page, size);

        String cleanKeyword = keyword == null ? null : keyword.trim();

        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        if (startDate != null) {
            startDateTime = startDate.atStartOfDay();
        }

        if (endDate != null) {
            endDateTime = endDate.atTime(23, 59, 59);
        }

        return reservationRepository.searchReservations(
                cleanKeyword,
                status,
                startDateTime,
                endDateTime,
                pageable
        );
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
        
        // Hủy luôn Order nếu có
        orderRepository.findByReservationId(reservationId).ifPresent(order -> {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        });
        
        reservationRepository.save(res);
    }

    @Override
    @Transactional
    public void confirmReservation(Long reservationId) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt bàn"));
        if (res.getStatus() != ReservationStatus.PENDING) {
            throw new RuntimeException("Chỉ có thể xác nhận đặt bàn ở trạng thái PENDING");
        }
        res.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(res);
    }

    @Override
    @Transactional
    public void adminCancelReservation(Long reservationId) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt bàn"));
        res.setStatus(ReservationStatus.CANCELLED);
        // Trả bàn về AVAILABLE
        DiningTable table = res.getTable();
        table.setStatus(TableStatus.AVAILABLE);
        diningTableRepository.save(table);
        
        // Hủy luôn Order nếu có
        orderRepository.findByReservationId(reservationId).ifPresent(order -> {
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.save(order);
        });
        
        reservationRepository.save(res);
    }

    @Override
    @Transactional
    public void completeReservation(Long reservationId) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt bàn"));
        res.setStatus(ReservationStatus.COMPLETED);
        reservationRepository.save(res);
    }

    @Override
    public List<Reservation> findAllSorted() {
        return reservationRepository.findAllByOrderByReservationTimeDesc();
    }

    @Override
    public Optional<Reservation> findById(Long id) {
        return reservationRepository.findById(id);
    }

    @Override
    public List<Reservation> findUpcoming(int limit) {
        // Lấy từ đầu ngày hôm nay để không bị mất các đặt bàn bị trễ một chút
        LocalDateTime startOfDay = java.time.LocalDate.now().atStartOfDay();
        List<ReservationStatus> activeStatuses = List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED);
        List<Reservation> upcoming = reservationRepository.findByReservationTimeGreaterThanEqualAndStatusInOrderByReservationTimeAsc(startOfDay, activeStatuses);
        return upcoming.stream().limit(limit).toList();
    }

    @Override
    @Transactional
    public void checkinReservation(Long reservationId, String staffUsername) {
        Reservation res = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt bàn"));

        if (res.getStatus() != ReservationStatus.CONFIRMED && res.getStatus() != ReservationStatus.PENDING) {
            throw new RuntimeException("Chỉ có thể check-in đặt bàn ở trạng thái PENDING hoặc CONFIRMED");
        }

        // Mark reservation as completed
        res.setStatus(ReservationStatus.COMPLETED);
        reservationRepository.save(res);

        // Set table to OCCUPIED
        DiningTable table = res.getTable();
        table.setStatus(TableStatus.OCCUPIED);
        diningTableRepository.save(table);

        // Check if there's already an order for this reservation (pre-ordered food)
        Optional<Order> existingOrder = orderRepository.findByReservationId(reservationId);
        if (existingOrder.isEmpty()) {
            // Create new order
            User staff = userRepository.findByUsername(staffUsername)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));
            Order order = Order.builder()
                    .table(table)
                    .user(staff)
                    .reservation(res)
                    .totalAmount(java.math.BigDecimal.ZERO)
                    .status(OrderStatus.SERVING)
                    .build();
            orderRepository.save(order);
        } else {
            Order order = existingOrder.get();
            if (order.getStatus() == OrderStatus.PENDING) {
                order.setStatus(OrderStatus.SERVING);
                orderRepository.save(order);
            }
        }
    }

    @Override
    @Transactional
    public Order createOrderForReservation(Long reservationId, String username) {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt bàn với ID: " + reservationId));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với username: " + username));

        // Hủy/xóa Order cũ đã liên kết với reservation này nếu có
        orderRepository.findByReservationId(reservationId).ifPresent(existingOrder -> {
            List<OrderItem> items = orderItemRepository.findByOrderId(existingOrder.getId());
            orderItemRepository.deleteAll(items);
            orderRepository.delete(existingOrder);
        });

        // Tạo Order mới từ giỏ hàng hiện tại
        Order order = Order.builder()
                .table(reservation.getTable())
                .user(user)
                .reservation(reservation)
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

        // Xóa giỏ hàng sau khi đã lưu đơn đặt món
        cartService.clear();

        return savedOrder;
    }
}
