package vn.edu.ptit.restaurant.service;

import vn.edu.ptit.restaurant.entity.Reservation;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservationService {
    Reservation createReservation(Long userId, Long tableId, LocalDateTime reservationTime, Integer numberOfGuests, String note);
    List<Reservation> findByUserId(Long userId);
    void cancelReservation(Long reservationId, Long userId);
}
