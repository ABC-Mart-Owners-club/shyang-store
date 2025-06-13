package product.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Getter
@AllArgsConstructor
@ToString
// 상품 정보
// 상품 실별값 + 입고일 => PK 느낌
public class Product {
    private String code; // 상품 식별값
    private LocalDate receiveDate; // 입고일

    private String name; // 상품명
    private int price; // 가격
    private int stock; // 재고

    public void deductStock(int amount) {
        if (amount > stock) {throw new IllegalStateException("수량이 부족합니다.");}
        stock -= amount;
    }

    public void refundStock(int amount) {
        stock += amount;
    }

    public int getDiscountPrice(){

        long daysSinceReceived = ChronoUnit.DAYS.between(receiveDate, LocalDate.now());

        if (daysSinceReceived >= 30) {
            return (int) (price * 0.5); // 50% 할인
        } else if (daysSinceReceived >= 7) {
            return (int) (price * 0.7); // 30% 할인
        } else {
            return price; // 할인 없음
        }
    }
}
