package stock.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
@ToString
// 재고 정보
// 입고일 기준으로 관리
public class Stock {
    private Long id; // 식별값
    private String productCode; // 상품 코드
    private int stock; // 재고
    private LocalDate receivedDate; // 입고일

    public void deductStock(int amount) {
        if (amount > stock) {throw new IllegalStateException("수량이 부족합니다.");}
        stock -= amount;
    }

    public void refundStock(int amount) {
        stock += amount;
    }
}
