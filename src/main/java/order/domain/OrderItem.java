package order.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import product.domain.Product;

@AllArgsConstructor
@Getter
public class OrderItem {
    private Product product; // 상품
    private int quantity; // 구매 수량
    private int buyPrice; // 구매 가격

    public int getProductTotalPrice() {
        return buyPrice * quantity;
    }
}
