package vn.edu.ptit.restaurant.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;
import vn.edu.ptit.restaurant.dto.TopSellingItemDTO;
import vn.edu.ptit.restaurant.entity.OrderItem;
import vn.edu.ptit.restaurant.entity.enums.OrderStatus;

import java.util.List;

import java.time.LocalDateTime;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    List<OrderItem> findByOrderId(Long orderId);

    @Query("""
            SELECT new vn.edu.ptit.restaurant.dto.TopSellingItemDTO(
                oi.menuItem.name,
                SUM(oi.quantity)
            )
            FROM OrderItem oi
            WHERE oi.order.status = :status
            GROUP BY oi.menuItem.id, oi.menuItem.name
            ORDER BY SUM(oi.quantity) DESC
            """)
    List<TopSellingItemDTO> findTopSellingItems(@Param("status") OrderStatus status, Pageable pageable);

    @Query("""
            SELECT new vn.edu.ptit.restaurant.dto.TopSellingItemDTO(
                oi.menuItem.name,
                SUM(oi.quantity)
            )
            FROM OrderItem oi
            WHERE oi.order.status = :status
            AND oi.order.createdAt >= :startDateTime
            AND oi.order.createdAt <= :endDateTime
            GROUP BY oi.menuItem.id, oi.menuItem.name
            ORDER BY SUM(oi.quantity) DESC
            """)
    List<TopSellingItemDTO> findTopSellingItemsBetween(
            @Param("status") OrderStatus status,
            @Param("startDateTime") LocalDateTime startDateTime,
            @Param("endDateTime") LocalDateTime endDateTime,
            Pageable pageable
    );
}