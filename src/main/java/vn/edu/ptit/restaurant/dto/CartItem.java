package vn.edu.ptit.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItem {
    private Long menuItemId;
    private String name;
    private String imageUrl;
    private BigDecimal price;
    private Integer quantity;
    private String note;
}
