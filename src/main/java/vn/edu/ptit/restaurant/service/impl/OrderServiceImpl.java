package vn.edu.ptit.restaurant.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.ptit.restaurant.entity.DiningTable;
import vn.edu.ptit.restaurant.entity.Order;
import vn.edu.ptit.restaurant.entity.User;
import vn.edu.ptit.restaurant.entity.enums.OrderStatus;
import vn.edu.ptit.restaurant.entity.enums.TableStatus;
import vn.edu.ptit.restaurant.repository.DiningTableRepository;
import vn.edu.ptit.restaurant.repository.OrderRepository;
import vn.edu.ptit.restaurant.repository.ReservationRepository;
import vn.edu.ptit.restaurant.repository.UserRepository;
import vn.edu.ptit.restaurant.service.OrderService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final DiningTableRepository diningTableRepository;
    private final UserRepository userRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public List<Order> findAll() {
        return orderRepository.findAll();
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    public Optional<Order> findById(Long id) {
        return orderRepository.findById(id);
    }

    @Override
    @Transactional
    public Order createOrder(Long tableId, String username) {
        DiningTable table = diningTableRepository.findById(tableId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bàn"));

        User staff = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy nhân viên"));

        Order order = Order.builder()
                .table(table)
                .user(staff)
                .totalAmount(BigDecimal.ZERO)
                .status(OrderStatus.PENDING)
                .build();

        return orderRepository.save(order);
    }

    @Override
    public Order save(Order order) {
        return orderRepository.save(order);
    }

    @Override
    @Transactional
    public void updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        order.setStatus(newStatus);
        orderRepository.save(order);

        if (newStatus == OrderStatus.SERVING && order.getTable() != null) {
            DiningTable table = order.getTable();
            table.setStatus(TableStatus.OCCUPIED);
            diningTableRepository.save(table);
        }
    }

    @Override
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        order.setStatus(OrderStatus.CANCELLED);

        DiningTable table = order.getTable();
        if (table != null) {
            table.setStatus(TableStatus.AVAILABLE);
            diningTableRepository.save(table);
        }

        if (order.getReservation() != null) {
            var reservation = order.getReservation();
            reservation.setStatus(vn.edu.ptit.restaurant.entity.enums.ReservationStatus.CANCELLED);
            reservationRepository.save(reservation);
        }

        orderRepository.save(order);
    }

    @Override
    @Transactional
    public void checkout(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        order.setStatus(OrderStatus.COMPLETED);

        if (order.getReservation() != null) {
            var reservation = order.getReservation();
            reservation.setStatus(vn.edu.ptit.restaurant.entity.enums.ReservationStatus.COMPLETED);
            reservationRepository.save(reservation);
        }

        DiningTable table = order.getTable();
        if (table != null) {
            table.setStatus(TableStatus.AVAILABLE);
            diningTableRepository.save(table);
        }

        orderRepository.save(order);
    }

    @Override
    public Page<Order> findPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findAll(pageable);
    }

    @Override
    public Page<Order> findByStatusPaginated(OrderStatus status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return orderRepository.findByStatus(status, pageable);
    }

    @Override
    public Page<Order> filterOrders(OrderStatus status,
                                    LocalDate startDate,
                                    LocalDate endDate,
                                    int page,
                                    int size) {
        return searchOrders(null, status, startDate, endDate, page, size);
    }

    @Override
    public Page<Order> searchOrders(String keyword,
                                    OrderStatus status,
                                    LocalDate startDate,
                                    LocalDate endDate,
                                    int page,
                                    int size) {
        Pageable pageable = PageRequest.of(page, size);

        String cleanKeyword = keyword == null ? null : keyword.trim().replace("#", "");

        Long keywordNumber = null;
        if (cleanKeyword != null && !cleanKeyword.isBlank()) {
            try {
                keywordNumber = Long.parseLong(cleanKeyword);
            } catch (NumberFormatException ignored) {
                keywordNumber = null;
            }
        }

        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        if (startDate != null) {
            startDateTime = startDate.atStartOfDay();
        }

        if (endDate != null) {
            endDateTime = endDate.atTime(23, 59, 59);
        }

        return orderRepository.searchOrders(
                cleanKeyword,
                keywordNumber,
                status,
                startDateTime,
                endDateTime,
                pageable
        );
    }
}