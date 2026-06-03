package vn.edu.ptit.restaurant.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.edu.ptit.restaurant.entity.Order;
import vn.edu.ptit.restaurant.entity.Payment;
import vn.edu.ptit.restaurant.entity.enums.PaymentMethod;
import vn.edu.ptit.restaurant.entity.enums.PaymentStatus;
import vn.edu.ptit.restaurant.repository.OrderRepository;
import vn.edu.ptit.restaurant.repository.PaymentRepository;
import vn.edu.ptit.restaurant.service.PaymentService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import vn.edu.ptit.restaurant.entity.enums.OrderStatus;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final OrderRepository orderRepository;

    @Override
    @Transactional
    public Payment createPayment(Long orderId, PaymentMethod method) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn"));

        if (order.getStatus() == vn.edu.ptit.restaurant.entity.enums.OrderStatus.CANCELLED) {
            throw new RuntimeException("Không thể thanh toán hóa đơn đã bị hủy");
        }

        if (paymentRepository.findByOrderId(orderId).isPresent()) {
            throw new RuntimeException("Hóa đơn này đã được thanh toán rồi");
        }

        java.math.BigDecimal remainingAmount = order.getTotalAmount().subtract(order.getAmountPaid());
        if (remainingAmount.compareTo(java.math.BigDecimal.ZERO) < 0) {
            remainingAmount = java.math.BigDecimal.ZERO;
        }

        Payment payment = Payment.builder()
                .order(order)
                .method(method)
                .status(PaymentStatus.PAID)
                .amount(remainingAmount)
                .build();

        // Cập nhật lại số tiền đã thanh toán của order
        order.setAmountPaid(order.getTotalAmount());
        orderRepository.save(order);

        return paymentRepository.save(payment);
    }

    @Override
    public Optional<Payment> findById(Long id) {
        return paymentRepository.findById(id);
    }

    @Override
    public Optional<Payment> findByOrderId(Long orderId) {
        return paymentRepository.findByOrderId(orderId);
    }

    @Override
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    @Override
    public List<Payment> findCompletedPayments() {
        return paymentRepository.findByStatusOrderByCreatedAtDesc(PaymentStatus.PAID);
    }

    @Override
    public List<Payment> findCompletedPaymentsByDateRange(LocalDate start, LocalDate end) {
        return paymentRepository.findByStatusAndCreatedAtBetweenOrderByCreatedAtDesc(
                PaymentStatus.PAID,
                start.atStartOfDay(),
                end.atTime(23, 59, 59)
        );
    }

    @Override
    public Page<Payment> searchPayments(String keyword,
                                        PaymentStatus status,
                                        PaymentMethod method,
                                        LocalDate startDate,
                                        LocalDate endDate,
                                        int page,
                                        int size) {
        Pageable pageable = PageRequest.of(page, size);

        LocalDateTime startDateTime = null;
        LocalDateTime endDateTime = null;

        if (startDate != null) {
            startDateTime = startDate.atStartOfDay();
        }

        if (endDate != null) {
            endDateTime = endDate.atTime(23, 59, 59);
        }

        String cleanKeyword = keyword == null ? "" : keyword.trim().replace("#", "");
        boolean keywordProvided = !cleanKeyword.isBlank();

        Long keywordNumber = null;

        if (keywordProvided) {
            try {
                keywordNumber = Long.parseLong(cleanKeyword);
            } catch (NumberFormatException e) {
                keywordNumber = null;
            }
        }

        return paymentRepository.searchPayments(
                status,
                method,
                startDateTime,
                endDateTime,
                keywordProvided,
                keywordNumber,
                pageable
        );
    }

    @Override
    @Transactional
    public void updateStatus(Long paymentId, PaymentStatus newStatus) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thanh toán"));

        if (newStatus == null) {
            throw new RuntimeException("Trạng thái thanh toán không hợp lệ");
        }

        PaymentStatus currentStatus = payment.getStatus();

        if (currentStatus == newStatus) {
            return;
        }

        if (currentStatus == PaymentStatus.PENDING) {
            if (newStatus != PaymentStatus.PAID && newStatus != PaymentStatus.FAILED) {
                throw new RuntimeException("Thanh toán đang chờ chỉ được chuyển sang PAID hoặc FAILED");
            }
        }

        if (currentStatus == PaymentStatus.PAID) {
            if (newStatus != PaymentStatus.REFUNDED) {
                throw new RuntimeException("Thanh toán đã thành công chỉ nên chuyển sang REFUNDED");
            }
        }

        if (currentStatus == PaymentStatus.FAILED) {
            throw new RuntimeException("Thanh toán thất bại không nên đổi trạng thái thủ công");
        }

        if (currentStatus == PaymentStatus.REFUNDED) {
            throw new RuntimeException("Thanh toán đã hoàn tiền, không thể đổi sang trạng thái khác");
        }

        payment.setStatus(newStatus);

        Order order = payment.getOrder();

        if (order != null) {
            if (newStatus == PaymentStatus.PAID) {
                order.setStatus(OrderStatus.COMPLETED);
                orderRepository.save(order);
            }

            if (newStatus == PaymentStatus.REFUNDED) {
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.save(order);
            }
        }

        paymentRepository.save(payment);
    }
}