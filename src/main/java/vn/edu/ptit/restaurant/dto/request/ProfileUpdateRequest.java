package vn.edu.ptit.restaurant.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ProfileUpdateRequest {

    @NotBlank(message = "Họ tên không được để trống.")
    private String fullName;

    @NotBlank(message = "Số điện thoại không được để trống.")
    @Pattern(regexp = "^[0-9]{9,11}$", message = "Số điện thoại không hợp lệ (phải gồm 9-11 chữ số).")
    private String phone;

    @NotBlank(message = "Địa chỉ email không được để trống.")
    @Email(message = "Địa chỉ email không hợp lệ.")
    private String email;
}
