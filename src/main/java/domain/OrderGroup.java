package domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
// 한번에 주문한 상품들 (like 장바구니 단위)
public class OrderGroup {

    private Long id; // 주문 그룹 식별 값
    private OrderHistory orderHistory; // 상품 단건
    private User user; // 장바구니 담은 유저

}
