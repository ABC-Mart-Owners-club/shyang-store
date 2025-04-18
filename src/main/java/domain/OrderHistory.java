package domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// 주문 내역
public class OrderHistory {
    private Long id; // 식별값
    private Product product; // 주문 상품
    private int quantity; // 주문 수량
    private int price; // 총 주문 가격
    private OrderType orderType; // 주문 타입
}
