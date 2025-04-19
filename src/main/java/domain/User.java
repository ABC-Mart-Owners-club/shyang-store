package domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@AllArgsConstructor
@Getter
@ToString
// 유저 정보
public class User {
    private String name; // 유저 이름 (유니크라고 가정)
    private int balance; // 유저 잔액

    public void chargeBalance(int amount) {
        if (amount > balance) {throw new IllegalStateException("잔액 부족");}
        balance -= amount;
    }

    public void refundBalance(int amount) {
        balance += amount;
    }
}
