package vn.edu.ptit.restaurant.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyRevenueDTO {

    private Integer year;

    private Integer month;

    private BigDecimal revenue;

    private Long orderCount;

    public String getLabel() {
        return "Tháng " + month + "/" + year;
    }
}