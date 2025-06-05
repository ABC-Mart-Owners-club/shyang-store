package dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class OrderRequestDto {
    private List<OrderProductInfo> orderProductInfoList;
    private List<OrderPaymentInfo> orderPaymentInfoList;
}
