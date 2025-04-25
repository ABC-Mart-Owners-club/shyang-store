package domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
// 주문 내역
public class OrderHistory {

    private Long id; // 식별값

    private int quantity; // 구매 수량
    private int price; // 구매 가격
    private Status status;

    private User user;
    private OrderGroup orderGroup;
    private Product product;

    public void cancelStatus(){
        this.status = Status.CANCELLED;
    }

    public boolean isCancelled() {
        return this.status == Status.CANCELLED;
    }

    public int getTotalPrice(){
        return this.price * this.quantity;
    }
}
