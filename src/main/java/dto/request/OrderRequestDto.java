package dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class OrderRequestDto {
    private List<OrderProductInfo> orderProductInfoList;
    private List<OrderPaymentInfo> orderPaymentInfoList;
}
