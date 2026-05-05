package vn.edu.ptit.restaurant.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.edu.ptit.restaurant.dto.ReportDTO;
import vn.edu.ptit.restaurant.entity.Order;
import vn.edu.ptit.restaurant.entity.enums.OrderStatus;
import vn.edu.ptit.restaurant.repository.OrderRepository;
import vn.edu.ptit.restaurant.repository.ReservationRepository;
import vn.edu.ptit.restaurant.service.ReportService;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final OrderRepository orderRepository;
    private final ReservationRepository reservationRepository;

    @Override
    public ReportDTO getGeneralReport() {
        List<Order> completedOrders = orderRepository.findByStatus(OrderStatus.COMPLETED);
        
        BigDecimal revenue = completedOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long orderCount = completedOrders.size();
        long reservationCount = reservationRepository.count();

        return ReportDTO.builder()
                .totalRevenue(revenue)
                .totalOrders(orderCount)
                .totalReservations(reservationCount)
                .build();
    }
}
