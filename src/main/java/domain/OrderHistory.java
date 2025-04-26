package domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
// 주문 내역
// 부분 결제 아니면 cashAmount, cardAmount Null
// 풀 현금이면 carName Null
public class OrderHistory {

    private Long id; // 식별값

    private int quantity; // 구매 수량
    private int price; // 구매 가격
    private Status status;

    private String userName;
    private Long groupId;
    private String productCode;

    private Integer cashAmount;
    private Integer cardAmount;
    private String cardName;

    public void cancelStatus(){
        this.status = Status.CANCELLED;
    }

    public boolean isCancelled() {
        return this.status == Status.CANCELLED;
    }

    public boolean isPartial() {
        return Status.PARTIAL_PAID.equals(this.status);
    }
}
