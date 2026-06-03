package vn.edu.ptit.restaurant.service;

import org.springframework.data.domain.Page;
import vn.edu.ptit.restaurant.entity.Payment;
import vn.edu.ptit.restaurant.entity.enums.PaymentMethod;
import vn.edu.ptit.restaurant.entity.enums.PaymentStatus;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PaymentService {

    Payment createPayment(Long orderId, PaymentMethod method);

    Optional<Payment> findById(Long id);

    Optional<Payment> findByOrderId(Long orderId);

    List<Payment> findAll();

    List<Payment> findCompletedPayments();

    List<Payment> findCompletedPaymentsByDateRange(LocalDate start, LocalDate end);

    Page<Payment> searchPayments(String keyword,
                                 PaymentStatus status,
                                 PaymentMethod method,
                                 LocalDate startDate,
                                 LocalDate endDate,
                                 int page,
                                 int size);

    void updateStatus(Long paymentId, PaymentStatus status);
}