package vn.edu.ptit.restaurant.service;

import vn.edu.ptit.restaurant.entity.Order;
import vn.edu.ptit.restaurant.entity.OrderItem;
import vn.edu.ptit.restaurant.entity.Reservation;

import java.util.List;

public interface MailService {
    void sendReservationConfirmation(Reservation reservation, Order order, List<OrderItem> orderItems);
}
