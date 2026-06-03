package vn.edu.ptit.restaurant.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.stereotype.Repository;
import vn.edu.ptit.restaurant.entity.Reservation;
import vn.edu.ptit.restaurant.entity.enums.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByUserIdOrderByReservationTimeDesc(Long userId);

    List<Reservation> findByUserIdAndReservationTimeBetweenOrderByReservationTimeDesc(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );

    List<Reservation> findByUserIdAndReservationTimeGreaterThanEqualOrderByReservationTimeDesc(
            Long userId,
            LocalDateTime start
    );

    long countByDeletedFalse();

    long countByDeletedFalseAndReservationTimeBetween(
        LocalDateTime start,
        LocalDateTime end
);

    List<Reservation> findByStatus(ReservationStatus status);

    List<Reservation> findByStatusOrderByReservationTimeAsc(ReservationStatus status);

    List<Reservation> findAllByOrderByReservationTimeDesc();
    boolean existsByTableId(Long tableId);

    List<Reservation> findByReservationTimeBetweenAndStatusIn(
            LocalDateTime start,
            LocalDateTime end,
            List<ReservationStatus> statuses
    );

    List<Reservation> findByReservationTimeGreaterThanEqualAndStatusInOrderByReservationTimeAsc(
            LocalDateTime start, 
            List<ReservationStatus> statuses
    );

    @Query("""
            SELECT r
            FROM Reservation r
            JOIN r.user u
            JOIN r.table t
            JOIN t.area a
            WHERE r.deleted = false
              AND (:status IS NULL OR r.status = :status)
              AND (:startDateTime IS NULL OR r.reservationTime >= :startDateTime)
              AND (:endDateTime IS NULL OR r.reservationTime <= :endDateTime)
              AND (
                    :keyword IS NULL OR :keyword = ''
                    OR LOWER(u.fullName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(u.phone) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(t.tableName) LIKE LOWER(CONCAT('%', :keyword, '%'))
                    OR LOWER(a.name) LIKE LOWER(CONCAT('%', :keyword, '%'))
                  )
            ORDER BY r.reservationTime DESC
            """)
    Page<Reservation> searchReservations(@Param("keyword") String keyword,
                                          @Param("status") ReservationStatus status,
                                          @Param("startDateTime") LocalDateTime startDateTime,
                                          @Param("endDateTime") LocalDateTime endDateTime,
                                          Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT r FROM Reservation r WHERE " +
           "(:status IS NULL OR r.status = :status) AND " +
           "(:date IS NULL OR DATE(r.reservationTime) = :date) AND " +
           "(:phone IS NULL OR r.user.phone LIKE CONCAT('%', :phone, '%')) " +
           "ORDER BY r.reservationTime DESC")
    List<Reservation> searchReservations(@org.springframework.data.repository.query.Param("status") ReservationStatus status,
                                         @org.springframework.data.repository.query.Param("date") java.time.LocalDate date,
                                         @org.springframework.data.repository.query.Param("phone") String phone);
}
