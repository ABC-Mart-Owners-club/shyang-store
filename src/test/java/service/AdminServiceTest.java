package service;

import dto.response.ProductSalesReport;
import order.domain.Order;
import order.domain.OrderStatus;
import order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pay.domain.Payment;
import pay.domain.PaymentMethod;
import pay.repository.PayRepository;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private PayRepository payRepository;
    @InjectMocks
    private AdminService adminService;

    @Test
    void getProductSalesReport_returnsCorrectSalesReportForValidOrders() { // Given OrderItem item1 = new OrderItem(new Product("P001", "Product 1"), 2, 100); OrderItem item2 = new OrderItem(new Product("P002", "Product 2"), 1, 200); OrderItem item3 = new OrderItem(new Product("P001", "Product 1"), 1, 100);
        Order order1 = new Order(List.of(item1, item2), OrderStatus.PAID);
        Order order2 = new Order(List.of(item3), OrderStatus.PAID);

        when(orderRepository.findAllByNotOrderStatus(OrderStatus.CANCELLED))
                .thenReturn(List.of(order1, order2));

        // When
        List<ProductSalesReport> result = adminService.getProductSalesReport();

        // Then
        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(r -> r.getProductCode().equals("P001") && r.getTotalSalesAmount() == 300));
        assertTrue(result.stream().anyMatch(r -> r.getProductCode().equals("P002") && r.getTotalSalesAmount() == 200));
    }

    @Test
    void getProductSalesReport_returnsEmptyListWhenNoValidOrdersExist() {
        // Given
        when(orderRepository.findAllByNotOrderStatus(OrderStatus.CANCELLED))
                .thenReturn(List.of());

        // When
        List<ProductSalesReport> result = adminService.getProductSalesReport();

        // Then
        assertTrue(result.isEmpty());
    }

    @Test
    void getCardSalesReport_returnsCorrectSalesReportForCardPayments() {
        // Given
        Payment payment1 = new Payment(new PaymentMethod(PaymentMethod.Type.CARD, PaymentMethod.CardType.VISA), 300);
        Payment payment2 = new Payment(new PaymentMethod(PaymentMethod.Type.CARD, PaymentMethod.CardType.MASTERCARD), 200);
        Payment payment3 = new Payment(new PaymentMethod(PaymentMethod.Type.CARD, PaymentMethod.CardType.VISA), 100);

        when(payRepository.findALLByPaymentType(PaymentMethod.Type.CARD))
                .thenReturn(List.of(payment1, payment2, payment3));

        // When
        Map<String, Integer> result = adminService.getCardSalesReport();

        // Then
        assertEquals(2, result.size());
        assertEquals(400, result.get("VISA"));
        assertEquals(200, result.get("MASTERCARD"));
    }

    @Test
    void getCardSalesReport_returnsEmptyMapWhenNoCardPaymentsExist() {
        // Given
        when(payRepository.findALLByPaymentType(PaymentMethod.Type.CARD))
                .thenReturn(List.of());

        // When
        Map<String, Integer> result = adminService.getCardSalesReport();

        // Then
        assertTrue(result.isEmpty());
    }

}