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
import vn.edu.ptit.restaurant.service.MailService;
import vn.edu.ptit.restaurant.service.ReservationService;
import vn.edu.ptit.restaurant.validator.customer.CartValidator;
import vn.edu.ptit.restaurant.validator.customer.ReservationValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import org.springframework.scheduling.annotation.Scheduled;

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
    private final ReservationValidator reservationValidator;
    private final CartValidator cartValidator;
    private final MailService mailService;

    @Override
    @Transactional
    public Reservation createReservation(Long userId, Long tableId, LocalDateTime reservationTime, Integer numberOfGuests, String note) {
        User user = userRepository.findById(userId).orElseThrow();
        DiningTable table = diningTableRepository.findById(tableId).orElseThrow();

        // Kiểm tra xem bàn đã được đặt trong khoảng thời gian này chưa (trước và sau 2 tiếng)
        reservationValidator.validateBookingDate(reservationTime);
        reservationValidator.validateTableAvailable(table, numberOfGuests);

        LocalDateTime start = reservationTime.minusHours(2);
        LocalDateTime end = reservationTime.plusHours(2);
        long overlappingCount = reservationRepository.countOverlappingReservations(
                tableId, start, end, List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED));
        
        if (overlappingCount > 0) {
            throw new RuntimeException("Bàn đã có người đặt trong khoảng thời gian này!");
        }

        // Bàn sẽ được cập nhật thành RESERVED tự động bởi Scheduled task 1 tiếng trước giờ đến

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
        Order savedOrder = null;
        List<OrderItem> savedOrderItems = new ArrayList<>();

        if (cartService.getCount() > 0) {
            Order order = Order.builder()
                    .table(table)
                    .user(user)
                    .reservation(savedReservation)
                    .totalAmount(cartService.getAmount())
                    .status(OrderStatus.PENDING)
                    .build();
            savedOrder = orderRepository.save(order);

            for (CartItem item : cartService.getItems()) {
                MenuItem menuItem = menuItemRepository.findById(item.getMenuItemId()).orElseThrow();
                OrderItem orderItem = OrderItem.builder()
                        .order(savedOrder)
                        .menuItem(menuItem)
                        .quantity(item.getQuantity())
                        .priceAtTime(item.getPrice())
                        .note(item.getNote())
                        .build();
                savedOrderItems.add(orderItemRepository.save(orderItem));
            }
            
            // Xóa giỏ hàng sau khi đặt bàn thành công
            cartService.clear();
        }

        mailService.sendReservationConfirmation(savedReservation, savedOrder, savedOrderItems);

        return savedReservation;
    }

    @Override
    public List<Reservation> findByUserId(Long userId) {
        java.time.LocalDateTime startOfToday = java.time.LocalDate.now().atStartOfDay();
        return reservationRepository.findByUserIdAndReservationTimeGreaterThanEqualAndStatusInOrderByReservationTimeDesc(
                userId, startOfToday, List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED));
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
        reservationValidator.validatePending(res);
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
    public Reservation findByIdForUser(Long id, String username) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt bàn với ID: " + id));
        reservationValidator.validateOwner(reservation, username);
        return reservation;
    }

    @Override
    @Transactional
    public void processDepositPayment(Long reservationId, String username) {
        Reservation reservation = findByIdForUser(reservationId, username);
        reservationValidator.validatePending(reservation);
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);
    }

    @Override
    @Transactional
    public void processFoodPayment(Long reservationId, String username, String paymentMode) {
        Reservation reservation = findByIdForUser(reservationId, username);
        reservationValidator.validatePending(reservation);
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservationRepository.save(reservation);

        Order order = orderRepository.findByReservationId(reservationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt món cho đặt bàn này."));
        if ("full".equalsIgnoreCase(paymentMode)) {
            order.setStatus(OrderStatus.CONFIRMED);
            order.setAmountPaid(order.getTotalAmount());
        } else {
            order.setStatus(OrderStatus.PENDING);
        }
        orderRepository.save(order);
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
    public List<Reservation> searchReservations(ReservationStatus status, java.time.LocalDate date, String phone) {
        return reservationRepository.searchReservations(status, date, phone);
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
            if (order.getStatus() == OrderStatus.PENDING || order.getStatus() == OrderStatus.CONFIRMED) {
                order.setStatus(OrderStatus.SERVING);
                orderRepository.save(order);
            }
        }
    }

    @Override
    @Transactional
    public Order createOrderForReservation(Long reservationId, String username) {
        cartValidator.validateNotEmpty(cartService);

        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đặt bàn với ID: " + reservationId));
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với username: " + username));

        // Hủy/xóa Order cũ đã liên kết với reservation này nếu có
        reservationValidator.validateOwner(reservation, username);

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
                    .note(item.getNote())
                    .build();
            orderItemRepository.save(orderItem);
        }

        // Xóa giỏ hàng sau khi đã lưu đơn đặt món
        cartService.clear();

        return savedOrder;
    }

    @Scheduled(fixedRate = 60000) // Chạy mỗi phút
    @Transactional
    public void autoUpdateReservationsAndTables() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneHourLater = now.plusHours(1);

        // 1. Tự động chuyển trạng thái bàn thành RESERVED trước 1 tiếng
        List<Reservation> upcoming = reservationRepository.findReservationsInTimeWindow(
                now, 
                oneHourLater,
                List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED)
        );

        for (Reservation res : upcoming) {
            DiningTable table = res.getTable();
            if (table != null && table.getStatus() == TableStatus.AVAILABLE) {
                table.setStatus(TableStatus.RESERVED);
                diningTableRepository.save(table);
            }
        }
        
        // 2. Tự động hủy các đơn đặt bàn quá hạn (sau 2 tiếng không check-in) và trả lại bàn
        List<Reservation> expired = reservationRepository.findReservationsInTimeWindow(
                now.minusDays(1), 
                now.minusHours(2),
                List.of(ReservationStatus.PENDING, ReservationStatus.CONFIRMED)
        );
        
        for (Reservation res : expired) {
            res.setStatus(ReservationStatus.CANCELLED);
            DiningTable table = res.getTable();
            if (table != null && table.getStatus() == TableStatus.RESERVED) {
                table.setStatus(TableStatus.AVAILABLE);
                diningTableRepository.save(table);
            }
            reservationRepository.save(res);
            
            // Hủy Order nếu có
            orderRepository.findByReservationId(res.getId()).ifPresent(order -> {
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
            });
        }
    }
}
