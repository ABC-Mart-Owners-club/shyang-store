package pay.domain;

import order.domain.Order;

public class Payment {
    private CardType cardType; // 카드 종류 (null 이면 현금)
    private Order order;
    private int payAmount; // 결제 금액
}
