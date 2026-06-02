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
    List<Reservation> findByStatus(ReservationStatus status);
    List<Reservation> findByStatusOrderByReservationTimeAsc(ReservationStatus status);
    List<Reservation> findAllByOrderByReservationTimeDesc();
    List<Reservation> findByReservationTimeBetweenAndStatusIn(LocalDateTime start, LocalDateTime end, List<ReservationStatus> statuses);
}
