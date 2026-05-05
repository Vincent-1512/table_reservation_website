package vn.edu.ptit.restaurant.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import vn.edu.ptit.restaurant.entity.Reservation;
import vn.edu.ptit.restaurant.repository.ReservationRepository;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;

@Service
public class PdfExportService {

    private final ReservationRepository reservationRepository;

    public PdfExportService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public ByteArrayInputStream exportReservationsToPdf() {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font fontTitle = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("BAO CAO DANH SACH DAT BAN", fontTitle);
            title.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(title);
            document.add(new Paragraph(" ")); // Empty line

            PdfPTable table = new PdfPTable(5);
            table.setWidthPercentage(100);
            table.addCell("Ma Dat Ban");
            table.addCell("Khach Hang");
            table.addCell("So Nguoi");
            table.addCell("Khu Vuc/Ban");
            table.addCell("Trang Thai");

            List<Reservation> reservations = reservationRepository.findAll();
            for (Reservation res : reservations) {
                table.addCell(String.valueOf(res.getId()));
                table.addCell(res.getUser().getFullName());
                table.addCell(String.valueOf(res.getNumberOfGuests()));
                table.addCell(res.getTable().getArea().getName() + " - " + res.getTable().getTableName());
                table.addCell(res.getStatus().name());
            }

            document.add(table);
            document.close();
            
        } catch (DocumentException ex) {
            ex.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
