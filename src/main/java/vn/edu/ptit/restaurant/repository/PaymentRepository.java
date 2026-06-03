package vn.edu.ptit.restaurant.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.edu.ptit.restaurant.entity.Payment;
import vn.edu.ptit.restaurant.entity.enums.PaymentMethod;
import vn.edu.ptit.restaurant.entity.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByOrderId(Long orderId);

    List<Payment> findByStatusOrderByCreatedAtDesc(PaymentStatus status);

    List<Payment> findByStatusAndCreatedAtBetweenOrderByCreatedAtDesc(
            PaymentStatus status,
            LocalDateTime start,
            LocalDateTime end
    );

    @Query("""
            SELECT p
            FROM Payment p
            JOIN p.order o
            WHERE (:status IS NULL OR p.status = :status)
              AND (:method IS NULL OR p.method = :method)
              AND (:startDateTime IS NULL OR p.createdAt >= :startDateTime)
              AND (:endDateTime IS NULL OR p.createdAt <= :endDateTime)
              AND (
                    :keywordProvided = false
                    OR (
                        :keywordNumber IS NOT NULL
                        AND (p.id = :keywordNumber OR o.id = :keywordNumber)
                    )
                  )
            ORDER BY p.createdAt DESC
            """)
    Page<Payment> searchPayments(@Param("status") PaymentStatus status,
                                 @Param("method") PaymentMethod method,
                                 @Param("startDateTime") LocalDateTime startDateTime,
                                 @Param("endDateTime") LocalDateTime endDateTime,
                                 @Param("keywordProvided") boolean keywordProvided,
                                 @Param("keywordNumber") Long keywordNumber,
                                 Pageable pageable);
}