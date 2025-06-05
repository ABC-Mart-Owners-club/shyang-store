package order.domain;

import pay.domain.Payment;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
// 결재 단위 묶음
public class Order {

    private String orderCode;

    private List<OrderItem> orderItems; // 주문 상품 목록

    private String userName; // 주문자 이름

    private OrderStatus orderStatus; // 주문 상태

    private List<Payment> payments; // 결제 정보


    // 주문에 포함된 내역 모두 취소
    public void cancelOrder() {

        if (!OrderStatus.PAID.equals(this.orderStatus)) {
            throw new IllegalStateException("이미 배송 시작 됐거나 이미 취소된 주문은 취소할 수 없습니다.");
        }
        orderStatus = OrderStatus.CANCELLED;
    }


    public void addPayment(Payment payment) {
        payments.add(payment);
    }

}
