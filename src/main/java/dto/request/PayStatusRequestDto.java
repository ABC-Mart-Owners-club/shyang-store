package dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
public class PayStatusRequestDto {
    private final Integer cashAmount;
    private final Integer cardAmount;
    private final String cardName; // 현금 풀 결제 일 경우 null

    public PayStatusRequestDto(Integer cashAmount, Integer cardAmount, String cardName) {
        this.cashAmount = cashAmount;
        this.cardAmount = cardAmount;
        this.cardName = cardName;
    }

    public boolean isPartial() {
        return cashAmount != null && cardAmount != null && cashAmount > 0 && cardAmount > 0 && cardName != null;
    }

    public boolean isFullCache() {
        return !isPartial() && cardName == null;
    }
}