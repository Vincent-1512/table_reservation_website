package vn.edu.ptit.restaurant.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ReservationRequest {

    @NotNull(message = "Vui lòng chọn bàn.")
    private Long tableId;

    @NotBlank(message = "Vui lòng chọn ngày đặt bàn.")
    private String reservationDate;

    @NotBlank(message = "Vui lòng chọn giờ đặt bàn.")
    private String reservationTime;

    @NotNull(message = "Vui lòng nhập số lượng khách.")
    @Min(value = 1, message = "Số lượng khách phải lớn hơn 0.")
    private Integer numberOfGuests;

    private String note;

    private String fullName;

    private String phone;

    private String email;
}
