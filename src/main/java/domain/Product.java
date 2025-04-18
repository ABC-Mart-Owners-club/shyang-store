package domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
// 상품 정보
public class Product {
    private Long id; // 식별 값
    private String name; // 상품명
    private int price; // 가격
    private int stockQuantity; // 재고 수량


    public void reduceStock(int quantity) {
        if (stockQuantity < quantity) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
        stockQuantity -= quantity;
    }

    public void addStock(int quantity) {
        stockQuantity += quantity;
    }
}
