package vn.edu.ptit.restaurant.service;

import vn.edu.ptit.restaurant.dto.ReportDTO;

public interface ReportService {
    ReportDTO getGeneralReport();

    long countOrders();

    long countReservations();

    long countUsers();

    long countTables();

    long countAvailableTables();

    long countOccupiedTables();

    long countReservedTables();
}
