package vn.edu.ptit.restaurant.validator.customer;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import vn.edu.ptit.restaurant.dto.request.ChangePasswordRequest;
import vn.edu.ptit.restaurant.entity.User;

@Component
public class CustomerProfileValidator {

    public void validatePasswordChange(User user, ChangePasswordRequest request, PasswordEncoder passwordEncoder) {
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("Mật khẩu hiện tại không chính xác.");
        }
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new RuntimeException("Mật khẩu mới và mật khẩu xác nhận không khớp.");
        }
    }

    public void validateAvatar(MultipartFile file) {
        if (file.isEmpty()) {
            throw new RuntimeException("File tải lên không được để trống.");
        }

        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("Chỉ cho phép tải lên các file định dạng hình ảnh.");
        }
    }
}
