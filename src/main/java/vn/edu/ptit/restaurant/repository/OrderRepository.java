package vn.edu.ptit.restaurant.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;
import vn.edu.ptit.restaurant.entity.Order;
import vn.edu.ptit.restaurant.entity.enums.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import vn.edu.ptit.restaurant.dto.MonthlyRevenueDTO;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findByStatus(OrderStatus status);

    List<Order> findByStatusAndCreatedAtBetween(
                OrderStatus status,
                LocalDateTime start,
                LocalDateTime end
        );

        long countByCreatedAtBetween(
                LocalDateTime start,
                LocalDateTime end
        );

    List<Order> findByTableIdAndStatus(Long tableId, OrderStatus status);

    Page<Order> findByStatus(OrderStatus status, Pageable pageable);

    Optional<Order> findByReservationId(Long reservationId);

    boolean existsByTableId(Long tableId);

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

    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);

    @Query("""
            SELECT o
            FROM Order o
            JOIN o.user u
            JOIN o.table t
            WHERE (:status IS NULL OR o.status = :status)
              AND (:startDateTime IS NULL OR o.createdAt >= :startDateTime)
              AND (:endDateTime IS NULL OR o.createdAt <= :endDateTime)
              AND (
                    :keyword IS NULL OR :keyword = ''
                    OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(u.username) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(t.tableName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR (:keywordNumber IS NOT NULL AND o.id = :keywordNumber)
                  )
            ORDER BY o.createdAt DESC
            """)
    Page<Order> searchOrders(@Param("keyword") String keyword,
                             @Param("keywordNumber") Long keywordNumber,
                             @Param("status") OrderStatus status,
                             @Param("startDateTime") LocalDateTime startDateTime,
                             @Param("endDateTime") LocalDateTime endDateTime,
                             Pageable pageable);


        @Query("""
                SELECT new vn.edu.ptit.restaurant.dto.MonthlyRevenueDTO(
                YEAR(o.createdAt),
                MONTH(o.createdAt),
                SUM(o.totalAmount),
                COUNT(o)
                )
                FROM Order o
                WHERE o.status = :status
                GROUP BY YEAR(o.createdAt), MONTH(o.createdAt)
                ORDER BY YEAR(o.createdAt) DESC, MONTH(o.createdAt) DESC
                """)
        List<MonthlyRevenueDTO> findMonthlyRevenue(@Param("status") OrderStatus status, Pageable pageable);

        @Query("""
                SELECT new vn.edu.ptit.restaurant.dto.MonthlyRevenueDTO(
                YEAR(o.createdAt),
                MONTH(o.createdAt),
                SUM(o.totalAmount),
                COUNT(o)
                )
                FROM Order o
                WHERE o.status = :status
                AND o.createdAt >= :startDateTime
                AND o.createdAt <= :endDateTime
                GROUP BY YEAR(o.createdAt), MONTH(o.createdAt)
                ORDER BY YEAR(o.createdAt) DESC, MONTH(o.createdAt) DESC
                """)
        List<MonthlyRevenueDTO> findMonthlyRevenueBetween(
                @Param("status") OrderStatus status,
                @Param("startDateTime") LocalDateTime startDateTime,
                @Param("endDateTime") LocalDateTime endDateTime,
                Pageable pageable
        );
}