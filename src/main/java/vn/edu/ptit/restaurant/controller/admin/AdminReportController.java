package vn.edu.ptit.restaurant.controller.admin;

import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.edu.ptit.restaurant.service.ExcelExportService;
import vn.edu.ptit.restaurant.service.PdfExportService;
import vn.edu.ptit.restaurant.service.ReportService;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Controller
@RequestMapping("/admin/reports")
@RequiredArgsConstructor
public class AdminReportController {

    private final ReportService reportService;
    private final ExcelExportService excelExportService;
    private final PdfExportService pdfExportService;

    @GetMapping
    public String dashboard(Model model) {
        model.addAttribute("report", reportService.getGeneralReport());
        model.addAttribute("totalOrders", reportService.countOrders());
        model.addAttribute("totalReservations", reportService.countReservations());
        model.addAttribute("totalUsers", reportService.countUsers());
        model.addAttribute("totalTables", reportService.countTables());
        model.addAttribute("availableTables", reportService.countAvailableTables());
        model.addAttribute("occupiedTables", reportService.countOccupiedTables());
        model.addAttribute("reservedTables", reportService.countReservedTables());
        return "admin/report/dashboard";
    }

    @GetMapping("/export/excel")
    public ResponseEntity<InputStreamResource> exportExcel() throws IOException {
        ByteArrayInputStream in = excelExportService.exportRevenueToExcel();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=revenue_report.xlsx");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }

    @GetMapping("/export/pdf")
    public ResponseEntity<InputStreamResource> exportPdf() {
        ByteArrayInputStream in = pdfExportService.exportReservationsToPdf();
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=reservation_report.pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(in));
    }
}
