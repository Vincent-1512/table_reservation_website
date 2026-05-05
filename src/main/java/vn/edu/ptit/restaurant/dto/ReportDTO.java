package vn.edu.ptit.restaurant.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ReportDTO {
    private BigDecimal totalRevenue;
    private long totalOrders;
    private long totalReservations;
}
