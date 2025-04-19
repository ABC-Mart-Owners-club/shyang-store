package domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
// 주문 내역
public class OrderHistory {

    private Long id; // 식별값
    private Long orderGroupId;
    private String productCode;

    private int quantity; // 구매 수량
    private int price; // 구매 가격

    private Status status;
    private String userName;

    public void cancelStatus(){
        this.status = Status.CANCELLED;
    }
}
