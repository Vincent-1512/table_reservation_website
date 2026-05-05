package vn.edu.ptit.restaurant.service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import vn.edu.ptit.restaurant.entity.Order;
import vn.edu.ptit.restaurant.entity.enums.OrderStatus;
import vn.edu.ptit.restaurant.repository.OrderRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

@Service
public class ExcelExportService {

    private final OrderRepository orderRepository;

    public ExcelExportService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public ByteArrayInputStream exportRevenueToExcel() throws IOException {
        String[] columns = {"Mã Hóa Đơn", "Bàn", "Nhân viên thu ngân", "Ngày giờ", "Tổng tiền (VNĐ)"};
        
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Doanh Thu");

            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setColor(IndexedColors.BLUE.getIndex());

            CellStyle headerCellStyle = workbook.createCellStyle();
            headerCellStyle.setFont(headerFont);

            // Row for Header
            Row headerRow = sheet.createRow(0);

            for (int col = 0; col < columns.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(columns[col]);
                cell.setCellStyle(headerCellStyle);
            }

            List<Order> orders = orderRepository.findByStatus(OrderStatus.COMPLETED);

            int rowIdx = 1;
            for (Order order : orders) {
                Row row = sheet.createRow(rowIdx++);

                row.createCell(0).setCellValue(order.getId());
                row.createCell(1).setCellValue(order.getTable().getTableName());
                row.createCell(2).setCellValue(order.getUser().getFullName());
                row.createCell(3).setCellValue(order.getCreatedAt().toString());
                row.createCell(4).setCellValue(order.getTotalAmount().doubleValue());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        }
    }
}
