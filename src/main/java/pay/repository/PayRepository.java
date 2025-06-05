package pay.repository;

import order.domain.Order;
import pay.domain.Payment;
import pay.domain.PaymentMethod;

import java.util.List;

public interface PayRepository {
    public void save(Payment payment);
    public void saveAll(List<Payment> payments);
    public List<Payment> findALLByPaymentType(PaymentMethod.Type paymentType);
    public List<Payment> findAll();
}
