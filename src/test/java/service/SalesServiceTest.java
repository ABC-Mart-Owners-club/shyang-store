package service;

import dto.request.OrderPaymentInfo;
import dto.request.OrderProductInfo;
import dto.request.OrderRequestDto;
import order.domain.Order;
import order.domain.OrderItem;
import order.domain.OrderStatus;
import order.repository.OrderRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pay.domain.PaymentMethod;
import pay.repository.PayRepository;
import product.domain.Product;
import product.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SalesServiceTest {

    @Mock
    private OrderRepository orderRepository;
    @Mock
    private ProductRepository productRepository;
    @Mock
    private PayRepository payRepository;
    @InjectMocks
    private SalesService salesService;

    @Test
    void order_createsOrderAndDeductsStockSuccessfully() {
        // Given
        String userName = "testUser";
        OrderRequestDto orderRequestDto = new OrderRequestDto(
                List.of(new OrderProductInfo("P001", 2, 100)),
                List.of(new OrderPaymentInfo(PaymentMethod.Type.CARD, PaymentMethod.CardType.VISA, 200))
        );

        Product product = new Product("P001", "Test Product", 10);
        OrderItem expectedOrderItem = new OrderItem(product, 2, 100);

        when(productRepository.findByCode("P001")).thenReturn(product);

        // When
        salesService.order(userName, orderRequestDto);

        // Then
        verify(productRepository).save(argThat(p -> p.getStock() == 8));
        verify(orderRepository).save(argThat(order ->
                order.getUserName().equals(userName) &&
                        order.getOrderItems().contains(expectedOrderItem) &&
                        order.getOrderStatus() == OrderStatus.PAID
        ));
        verify(payRepository).saveAll(anyList());
    }

    @Test
    void order_throwsExceptionWhenProductStockIsInsufficient() {
        // Given
        String userName = "testUser";
        OrderRequestDto orderRequestDto = new OrderRequestDto(
                List.of(new OrderProductInfo("P001", 15, 100)),
                List.of(new OrderPaymentInfo(PaymentMethod.Type.CARD, PaymentMethod.CardType.VISA, 1500))
        );

        Product product = new Product("P001", "Test Product", 10);

        when(productRepository.findByCode("P001")).thenReturn(product);

        // When / Then
        assertThrows(IllegalArgumentException.class, () -> salesService.order(userName, orderRequestDto));
        verify(productRepository, never()).save(any());
        verify(orderRepository, never()).save(any());
        verify(payRepository, never()).saveAll(anyList());
    }

    @Test
    void order_createsOrderWithMultipleProductsAndPayments() {
        // Given
        String userName = "testUser";
        OrderRequestDto orderRequestDto = new OrderRequestDto(
                List.of(
                        new OrderProductInfo("P001", 2, 100),
                        new OrderProductInfo("P002", 1, 200)
                ),
                List.of(
                        new OrderPaymentInfo(PaymentMethod.Type.CARD, PaymentMethod.CardType.VISA, 300),
                        new OrderPaymentInfo(PaymentMethod.Type.CASH, null, 100)
                )
        );

        Product product1 = new Product("P001", "Product 1", 10);
        Product product2 = new Product("P002", "Product 2", 5);

        when(productRepository.findByCode("P001")).thenReturn(product1);
        when(productRepository.findByCode("P002")).thenReturn(product2);

        // When
        salesService.order(userName, orderRequestDto);

        // Then
        verify(productRepository).save(argThat(p -> p.getCode().equals("P001") && p.getStock() == 8));
        verify(productRepository).save(argThat(p -> p.getCode().equals("P002") && p.getStock() == 4));
        verify(orderRepository).save(argThat(order ->
                order.getOrderItems().size() == 2 &&
                        order.getPayments().size() == 2
        ));
        verify(payRepository).saveAll(anyList());
    }

    @Test void cancelOrders_cancelsAllPaidOrdersAndRestoresStock() { // Given String userName = "testUser"; Product product1 = new Product("P001", "Product 1", 5); Product product2 = new Product("P002", "Product 2", 10);
        OrderItem orderItem1 = new OrderItem(product1, 2, 100);
        OrderItem orderItem2 = new OrderItem(product2, 3, 200);

        Order order1 = new Order("O001", List.of(orderItem1), userName, OrderStatus.PAID, new ArrayList<>());
        Order order2 = new Order("O002", List.of(orderItem2), userName, OrderStatus.PAID, new ArrayList<>());

        when(orderRepository.findByUserNameWhereOrderStatus(userName, OrderStatus.PAID))
                .thenReturn(List.of(order1, order2));

        // When
        salesService.cancelOrders(userName);

        // Then
        verify(orderRepository).saveAll(argThat(orders ->
                orders.stream().allMatch(order -> order.getOrderStatus() == OrderStatus.CANCELLED)
        ));
        verify(productRepository).save(argThat(p -> p.getCode().equals("P001") && p.getStock() == 7));
        verify(productRepository).save(argThat(p -> p.getCode().equals("P002") && p.getStock() == 13));
    }

    @Test
    void cancelOrders_doesNothingWhenNoPaidOrdersExist() {
        // Given
        String userName = "testUser";

        when(orderRepository.findByUserNameWhereOrderStatus(userName, OrderStatus.PAID))
                .thenReturn(List.of());

        // When
        salesService.cancelOrders(userName);

        // Then
        verify(orderRepository, never()).saveAll(anyList());
        verify(productRepository, never()).save(any());
    }

    @Test void cancelOrder_cancelsOrderAndRestoresStockSuccessfully() { // Given String orderCode = "O001"; Product product1 = new Product("P001", "Product 1", 5); Product product2 = new Product("P002", "Product 2", 10);
        OrderItem orderItem1 = new OrderItem(product1, 2, 100);
        OrderItem orderItem2 = new OrderItem(product2, 3, 200);

        Order order = new Order(orderCode, List.of(orderItem1, orderItem2), "testUser", OrderStatus.PAID, new ArrayList<>());

        when(orderRepository.findByOrderCode(orderCode)).thenReturn(order);

        // When
        salesService.cancelOrder(orderCode);

        // Then
        verify(orderRepository).save(argThat(o ->
                o.getOrderStatus() == OrderStatus.CANCELLED
        ));
        verify(productRepository).save(argThat(p -> p.getCode().equals("P001") && p.getStock() == 7));
        verify(productRepository).save(argThat(p -> p.getCode().equals("P002") && p.getStock() == 13));
    }

    @Test
    void cancelOrder_doesNothingWhenOrderIsNotPaid() {
        // Given
        String orderCode = "O002";
        Product product = new Product("P001", "Product 1", 5);

        OrderItem orderItem = new OrderItem(product, 2, 100);
        Order order = new Order(orderCode, List.of(orderItem), "testUser", OrderStatus.SHIPPED, new ArrayList<>());

        when(orderRepository.findByOrderCode(orderCode)).thenReturn(order);

        // When
        salesService.cancelOrder(orderCode);

        // Then
        verify(orderRepository, never()).save(any());
        verify(productRepository, never()).save(any());
    }
}