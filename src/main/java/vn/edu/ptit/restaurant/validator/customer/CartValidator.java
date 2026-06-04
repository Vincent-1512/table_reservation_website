package vn.edu.ptit.restaurant.validator.customer;

import org.springframework.stereotype.Component;
import vn.edu.ptit.restaurant.service.CartService;

@Component
public class CartValidator {

    public void validateNotEmpty(CartService cartService) {
        if (cartService.getItems().isEmpty()) {
            throw new RuntimeException("Vui lòng chọn ít nhất một món ăn trước khi hoàn tất.");
        }
    }
}
