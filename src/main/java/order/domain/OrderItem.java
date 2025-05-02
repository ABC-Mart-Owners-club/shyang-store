package order.domain;

import product.domain.Product;

public class OrderItem {
    private Product product; // 상품 ID
    private int quantity; // 구매 수량
    private int buyPrice; // 구매 가격
}
