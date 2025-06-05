package service;

import dto.response.ProductSalesReport;
import order.domain.Order;
import order.domain.OrderItem;
import order.domain.OrderStatus;
import order.repository.OrderRepository;
import pay.domain.Payment;
import pay.domain.PaymentMethod;
import pay.repository.PayRepository;
import product.domain.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminService {

    private OrderRepository orderRepository;
    private PayRepository payRepository;

    // 1. 상품 별 판매 금액 조회
    public List<ProductSalesReport> getProductSalesReport() {
        List<Order> validOrders = orderRepository.findAllByNotOrderStatus(OrderStatus.CANCELLED);

        Map<String, ProductSalesReport> reportMap = new HashMap<>();

        for (Order order : validOrders) {
            for (OrderItem item : order.getOrderItems()) {
                Product product = item.getProduct();
                String code = product.getCode();
                String name = product.getName();
                int salesAmount = item.getProductTotalPrice();

                reportMap.compute(code, (key, report) -> {
                    if (report == null) {
                        return new ProductSalesReport(code, name, salesAmount);
                    }
                    report.addToTotalSales(salesAmount); // 누적
                    return report;
                });
            }
        }

        return new ArrayList<>(reportMap.values());
    }


    // 2. 카드사별 판매 금액 조회
    public Map<String, Integer> getCardSalesReport() {

        List<Payment> cardPayments = payRepository.findALLByPaymentType(PaymentMethod.Type.CARD);

        Map<String, Integer> cardSalesMap = new HashMap<>();
        for (Payment cardPayment : cardPayments) {
            PaymentMethod.CardType cardType = cardPayment.getPaymentMethod().getCardType();
            int totalSalesAmount = cardPayment.getPayAmount();

            cardSalesMap.compute(cardType.name(), (key, report) -> {
                if (report == null) {
                    return totalSalesAmount;
                }
                return report + totalSalesAmount; // 누적
            });
        }

        return cardSalesMap;
    }

}
