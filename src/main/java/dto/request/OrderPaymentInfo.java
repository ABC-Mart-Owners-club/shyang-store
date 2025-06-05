package dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import pay.domain.PaymentMethod;

@Getter
@AllArgsConstructor
public class OrderPaymentInfo {
    private PaymentMethod.CardType cardType; // 카드 결제일 경우 카드 종류 (카드 결제 아니면 null)
    private PaymentMethod.Type type; // 결제 수단
    private int payAmount; // 결제 금액
}
