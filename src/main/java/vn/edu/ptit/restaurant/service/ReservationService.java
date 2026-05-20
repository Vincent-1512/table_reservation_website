package vn.edu.ptit.restaurant.service;

import vn.edu.ptit.restaurant.entity.Reservation;
import vn.edu.ptit.restaurant.entity.enums.ReservationStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReservationService {
    Reservation createReservation(Long userId, Long tableId, LocalDateTime reservationTime, Integer numberOfGuests, String note);
    List<Reservation> findByUserId(Long userId);
    List<Reservation> findByStatus(ReservationStatus status);
    List<Reservation> findAll();
    List<Reservation> findAllSorted();
    Optional<Reservation> findById(Long id);
    List<Reservation> findUpcoming(int limit);
    void cancelReservation(Long reservationId, Long userId);
    // Admin/Staff actions
    void confirmReservation(Long reservationId);
    void adminCancelReservation(Long reservationId);
    void completeReservation(Long reservationId);
    void checkinReservation(Long reservationId, String staffUsername);
}
