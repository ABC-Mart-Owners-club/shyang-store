package order.domain;

public enum OrderStatus {
    PAID, // 결제 완료
    SHIPPED, // 배송 시작
    DELIVERED, // 배송 완료
    CANCELLED; // 주문 취소
}
