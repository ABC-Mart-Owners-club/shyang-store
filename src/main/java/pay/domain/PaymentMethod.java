package pay.domain;

import lombok.Getter;

@Getter
public class PaymentMethod {

    private final Type type;
    private final CardType cardType;

    public enum Type {
        CARD, CASH
    }

    public enum CardType {
        SHIN_HAN, KB, HANA
    }

    public PaymentMethod(Type type, CardType cardType) {
        this.type = type;
        this.cardType = cardType;
    }

    public boolean isCard() {
        return type == Type.CARD;
    }

    public boolean isCash() {
        return type == Type.CASH;
    }
}
