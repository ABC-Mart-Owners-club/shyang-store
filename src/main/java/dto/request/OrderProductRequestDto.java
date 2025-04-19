package dto.request;

import domain.Product;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class OrderProductRequestDto {

    private String productCode;
    private int quantity;
}
