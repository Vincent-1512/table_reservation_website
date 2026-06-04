package vn.edu.ptit.restaurant.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import vn.edu.ptit.restaurant.entity.Order;
import vn.edu.ptit.restaurant.entity.OrderItem;
import vn.edu.ptit.restaurant.entity.Reservation;
import vn.edu.ptit.restaurant.service.MailService;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MailService {

    private static final long DEPOSIT_AMOUNT = 100000L;

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username:}")
    private String mailFrom;

    @Override
    public void sendReservationConfirmation(Reservation reservation, Order order, List<OrderItem> orderItems) {
        try {
            String recipient = reservation.getUser().getEmail();
            if (recipient == null || recipient.isBlank()) {
                log.warn("Skip reservation confirmation email because user email is empty. reservationId={}", reservation.getId());
                return;
            }

            SimpleMailMessage message = new SimpleMailMessage();
            if (mailFrom != null && !mailFrom.isBlank()) {
                message.setFrom(mailFrom);
            }
            message.setTo(recipient);
            message.setSubject("Lumina - Xac nhan dat ban #" + reservation.getId());
            message.setText(buildReservationConfirmationText(reservation, order, orderItems));

            javaMailSender.send(message);
            log.info("Sent reservation confirmation email. reservationId={}, recipient={}", reservation.getId(), recipient);
        } catch (Exception e) {
            log.error("Failed to send reservation confirmation email. reservationId={}", reservation.getId(), e);
        }
    }

    private String buildReservationConfirmationText(Reservation reservation, Order order, List<OrderItem> orderItems) {
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

        StringBuilder content = new StringBuilder();
        content.append("Xin chao ").append(nullToDefault(reservation.getUser().getFullName(), "quy khach")).append(",\n\n");
        content.append("Lumina da ghi nhan phieu dat ban cua quy khach.\n\n");
        content.append("THONG TIN DAT BAN\n");
        content.append("- Ma dat ban: #RES-").append(reservation.getId()).append("\n");
        content.append("- Ten khach hang: ").append(nullToDefault(reservation.getUser().getFullName(), "Chua cap nhat")).append("\n");
        content.append("- So dien thoai: ").append(nullToDefault(reservation.getUser().getPhone(), "Chua cap nhat")).append("\n");
        content.append("- Ngay dat: ").append(reservation.getReservationTime().format(dateFormatter)).append("\n");
        content.append("- Gio den: ").append(reservation.getReservationTime().format(timeFormatter)).append("\n");
        content.append("- So luong khach: ").append(reservation.getNumberOfGuests()).append("\n");
        content.append("- Ban/khu vuc: ").append(buildTableText(reservation)).append("\n");
        content.append("- Trang thai dat ban: ").append(reservation.getStatus()).append("\n");
        content.append("- Tien coc du kien: ").append(formatMoney(BigDecimal.valueOf(DEPOSIT_AMOUNT))).append("\n");

        if (order != null && orderItems != null && !orderItems.isEmpty()) {
            content.append("\nMON AN DAT TRUOC\n");
            for (OrderItem item : orderItems) {
                BigDecimal lineTotal = item.getPriceAtTime().multiply(BigDecimal.valueOf(item.getQuantity()));
                content.append("- ")
                        .append(item.getMenuItem().getName())
                        .append(" x ")
                        .append(item.getQuantity())
                        .append(" = ")
                        .append(formatMoney(lineTotal));
                if (item.getNote() != null && !item.getNote().isBlank()) {
                    content.append(" (Ghi chu: ").append(item.getNote()).append(")");
                }
                content.append("\n");
            }
            content.append("- Tong tien mon an: ").append(formatMoney(order.getTotalAmount())).append("\n");
        } else {
            content.append("\nQuy khach chua dat truoc mon an.\n");
        }

        content.append("\nCam on quy khach da lua chon Lumina.\n");
        content.append("Neu can thay doi thong tin dat ban, vui long lien he nha hang de duoc ho tro.\n");

        return content.toString();
    }

    private String buildTableText(Reservation reservation) {
        if (reservation.getTable() == null) {
            return "Chua xac dinh";
        }
        String tableName = reservation.getTable().getTableName();
        String areaName = reservation.getTable().getArea() != null ? reservation.getTable().getArea().getName() : null;
        if (areaName == null || areaName.isBlank()) {
            return tableName;
        }
        return tableName + " - " + areaName;
    }

    private String formatMoney(BigDecimal amount) {
        if (amount == null) {
            amount = BigDecimal.ZERO;
        }
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return formatter.format(amount);
    }

    private String nullToDefault(String value, String defaultValue) {
        return value == null || value.isBlank() ? defaultValue : value;
    }
}
