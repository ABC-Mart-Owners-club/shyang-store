package pay.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import order.domain.Order;

@Getter
@AllArgsConstructor
public class Payment {
    private PaymentMethod paymentMethod; // 결제 종류
    private Order order;
    private int payAmount; // 결제 금액
}
