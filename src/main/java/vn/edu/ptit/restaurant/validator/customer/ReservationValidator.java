package vn.edu.ptit.restaurant.validator.customer;

import org.springframework.stereotype.Component;
import vn.edu.ptit.restaurant.entity.DiningTable;
import vn.edu.ptit.restaurant.entity.Reservation;
import vn.edu.ptit.restaurant.entity.enums.ReservationStatus;
import vn.edu.ptit.restaurant.entity.enums.TableStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Component
public class ReservationValidator {

    public void validateBookingDate(LocalDateTime reservationTime) {
        LocalDate bookingDate = reservationTime.toLocalDate();
        LocalDate today = LocalDate.now();

        if (bookingDate.isBefore(today)) {
            throw new RuntimeException("Ngày đặt bàn không hợp lệ.");
        }
        if (bookingDate.isAfter(today.plusDays(14))) {
            throw new RuntimeException("Nhà hàng hiện chỉ nhận đặt bàn trước tối đa 14 ngày.");
        }
    }

    public void validateTableAvailable(DiningTable table, Integer numberOfGuests) {
        if (table.getStatus() != TableStatus.AVAILABLE || table.getCapacity() < numberOfGuests) {
            throw new RuntimeException("Hiện không còn bàn phù hợp cho số lượng khách và khung giờ bạn đã chọn.");
        }
    }

    public void validateOwner(Reservation reservation, String username) {
        if (!reservation.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Không có quyền thao tác với đặt bàn này.");
        }
    }

    public void validatePending(Reservation reservation) {
        if (reservation.getStatus() != ReservationStatus.PENDING) {
            throw new RuntimeException("Chỉ có thể xác nhận đặt bàn ở trạng thái PENDING");
        }
    }
}
