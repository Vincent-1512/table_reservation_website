package vn.edu.ptit.restaurant.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.edu.ptit.restaurant.entity.Reservation;
import vn.edu.ptit.restaurant.entity.enums.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUserIdOrderByReservationTimeDesc(Long userId);
    List<Reservation> findByUserIdAndReservationTimeBetweenOrderByReservationTimeDesc(Long userId, java.time.LocalDateTime start, java.time.LocalDateTime end);
    List<Reservation> findByUserIdAndReservationTimeGreaterThanEqualOrderByReservationTimeDesc(Long userId, java.time.LocalDateTime start);
    List<Reservation> findByUserIdAndReservationTimeGreaterThanEqualAndStatusInOrderByReservationTimeDesc(Long userId, java.time.LocalDateTime start, List<ReservationStatus> statuses);
    List<Reservation> findByStatus(ReservationStatus status);
    List<Reservation> findByStatusOrderByReservationTimeAsc(ReservationStatus status);
    List<Reservation> findAllByOrderByReservationTimeDesc();
    List<Reservation> findByReservationTimeBetweenAndStatusIn(LocalDateTime start, LocalDateTime end, List<ReservationStatus> statuses);
    List<Reservation> findByReservationTimeGreaterThanEqualAndStatusInOrderByReservationTimeAsc(LocalDateTime start, List<ReservationStatus> statuses);

    @org.springframework.data.jpa.repository.Query("SELECT r FROM Reservation r WHERE " +
           "(:status IS NULL OR r.status = :status) AND " +
           "(:date IS NULL OR DATE(r.reservationTime) = :date) AND " +
           "(:phone IS NULL OR r.user.phone LIKE CONCAT('%', :phone, '%')) " +
           "ORDER BY r.reservationTime DESC")
    List<Reservation> searchReservations(@org.springframework.data.repository.query.Param("status") ReservationStatus status,
                                         @org.springframework.data.repository.query.Param("date") java.time.LocalDate date,
                                         @org.springframework.data.repository.query.Param("phone") String phone);
}
