package product.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@ToString
// 상품 정보
public class Product {
    private String code; // 식별 값
    private String name; // 상품명
    private int price; // 가격
}
