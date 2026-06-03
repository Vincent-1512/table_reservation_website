package vn.edu.ptit.restaurant.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import vn.edu.ptit.restaurant.dto.MonthlyRevenueDTO;
import vn.edu.ptit.restaurant.dto.ReportDTO;
import vn.edu.ptit.restaurant.dto.TopSellingItemDTO;
import vn.edu.ptit.restaurant.entity.Order;
import vn.edu.ptit.restaurant.entity.enums.OrderStatus;
import vn.edu.ptit.restaurant.entity.enums.TableStatus;
import vn.edu.ptit.restaurant.repository.DiningTableRepository;
import vn.edu.ptit.restaurant.repository.OrderItemRepository;
import vn.edu.ptit.restaurant.repository.OrderRepository;
import vn.edu.ptit.restaurant.repository.ReservationRepository;
import vn.edu.ptit.restaurant.repository.UserRepository;
import vn.edu.ptit.restaurant.service.ReportService;

import java.math.BigDecimal;
import java.util.List;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final ReservationRepository reservationRepository;
    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final DiningTableRepository diningTableRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    public ReportDTO getGeneralReport() {
        return getGeneralReport(null, null);
    }

    @Override
    public long countOrders() {
        return countOrders(null, null);
    }

    @Override
    public long countReservations() {
        return countReservations(null, null);
    }

    @Override
    public long countUsers() {
        return userRepository.countByDeletedFalse();
    }

    @Override
    public long countTables() {
        return diningTableRepository.count();
    }

    @Override
    public long countAvailableTables() {
        return diningTableRepository.countByStatus(TableStatus.AVAILABLE);
    }

    @Override
    public long countOccupiedTables() {
        return diningTableRepository.countByStatus(TableStatus.OCCUPIED);
    }

    @Override
    public long countReservedTables() {
        return diningTableRepository.countByStatus(TableStatus.RESERVED);
    }


    @Override
    public List<TopSellingItemDTO> getTopSellingItems() {
        return getTopSellingItems(null, null);
    }

    @Override
    public ReportDTO getGeneralReport(LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = toStartDateTime(startDate);
        LocalDateTime endDateTime = toEndDateTime(endDate);

        List<Order> completedOrders = orderRepository.findByStatusAndCreatedAtBetween(
                OrderStatus.COMPLETED,
                startDateTime,
                endDateTime
        );

        BigDecimal revenue = completedOrders.stream()
                .map(Order::getTotalAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long orderCount = completedOrders.size();

        long reservationCount = reservationRepository.countByDeletedFalseAndReservationTimeBetween(
                startDateTime,
                endDateTime
        );

        return ReportDTO.builder()
                .totalRevenue(revenue)
                .totalOrders(orderCount)
                .totalReservations(reservationCount)
                .build();
    }

    @Override
    public long countOrders(LocalDate startDate, LocalDate endDate) {
        return orderRepository.countByCreatedAtBetween(
                toStartDateTime(startDate),
                toEndDateTime(endDate)
        );
    }

    @Override
    public long countReservations(LocalDate startDate, LocalDate endDate) {
        return reservationRepository.countByDeletedFalseAndReservationTimeBetween(
                toStartDateTime(startDate),
                toEndDateTime(endDate)
        );
    }

    @Override
    public List<MonthlyRevenueDTO> getMonthlyRevenue() {
        return getMonthlyRevenue(null, null);
    }

    @Override
    public List<MonthlyRevenueDTO> getMonthlyRevenue(LocalDate startDate, LocalDate endDate) {
        return orderRepository.findMonthlyRevenueBetween(
                OrderStatus.COMPLETED,
                toStartDateTime(startDate),
                toEndDateTime(endDate),
                PageRequest.of(0, 6)
        );
    }

    @Override
    public List<TopSellingItemDTO> getTopSellingItems(LocalDate startDate, LocalDate endDate) {
        return orderItemRepository.findTopSellingItemsBetween(
                OrderStatus.COMPLETED,
                toStartDateTime(startDate),
                toEndDateTime(endDate),
                PageRequest.of(0, 5)
        );
    }

    private LocalDateTime toStartDateTime(LocalDate startDate) {
        if (startDate == null) {
            return LocalDateTime.of(1970, 1, 1, 0, 0);
        }

        return startDate.atStartOfDay();
    }

    private LocalDateTime toEndDateTime(LocalDate endDate) {
        if (endDate == null) {
            return LocalDateTime.now().plusYears(1);
        }

        return endDate.atTime(LocalTime.MAX);
    }
}