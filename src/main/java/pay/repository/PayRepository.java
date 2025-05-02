package pay.repository;

import order.domain.Order;
import pay.domain.Payment;

import java.util.List;

public interface PayRepository {
    public Payment save(Payment payment);
    public List<Payment> findAll();
}
