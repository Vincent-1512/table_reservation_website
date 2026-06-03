package vn.edu.ptit.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TopSellingItemDTO {

    private String itemName;

    private Long totalQuantity;
}