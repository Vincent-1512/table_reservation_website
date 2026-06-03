package vn.edu.ptit.restaurant.service;

import vn.edu.ptit.restaurant.dto.MonthlyRevenueDTO;
import vn.edu.ptit.restaurant.dto.ReportDTO;
import vn.edu.ptit.restaurant.dto.TopSellingItemDTO;

import java.time.LocalDate;
import java.util.List;

public interface ReportService {

    ReportDTO getGeneralReport();

    ReportDTO getGeneralReport(LocalDate startDate, LocalDate endDate);

    long countOrders();

    long countOrders(LocalDate startDate, LocalDate endDate);

    long countReservations();

    long countReservations(LocalDate startDate, LocalDate endDate);

    long countUsers();

    long countTables();

    long countAvailableTables();

    long countOccupiedTables();

    long countReservedTables();

    List<MonthlyRevenueDTO> getMonthlyRevenue();

    List<MonthlyRevenueDTO> getMonthlyRevenue(LocalDate startDate, LocalDate endDate);

    List<TopSellingItemDTO> getTopSellingItems();

    List<TopSellingItemDTO> getTopSellingItems(LocalDate startDate, LocalDate endDate);
}