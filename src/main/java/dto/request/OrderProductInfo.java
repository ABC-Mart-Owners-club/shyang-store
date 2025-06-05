package dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderProductInfo {
    private String productCode;
    private int quantity;
    private int buyPrice;
}
