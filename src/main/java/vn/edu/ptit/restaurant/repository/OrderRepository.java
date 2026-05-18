package vn.edu.ptit.restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.ptit.restaurant.entity.Order;
import vn.edu.ptit.restaurant.entity.enums.OrderStatus;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByTableIdAndStatus(Long tableId, OrderStatus status);
    Page<Order> findByStatus(OrderStatus status, Pageable pageable);
    Optional<Order> findByReservationId(Long reservationId);

    Page<Order> findByCreatedAtBetween(
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );

    Page<Order> findByStatusAndCreatedAtBetween(
            OrderStatus status,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable
    );
}


