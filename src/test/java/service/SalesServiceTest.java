package service;

import dto.request.OrderPaymentInfo;
import dto.request.OrderProductInfo;
import dto.request.OrderRequestDto;
import order.domain.Order;
import order.domain.OrderItem;
import order.domain.OrderStatus;
import order.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import pay.domain.Payment;
import pay.domain.PaymentMethod;
import pay.repository.PayRepository;
import product.domain.Product;
import product.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
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

    @Captor
    private ArgumentCaptor<Order> orderCaptor;

    private Product product1;
    private Product product2;
    private Product product3;

    @BeforeEach
    public void setUp() {
        // 상품 초기화
        product1 = new Product("P001", "Product 1", 100_000, 100);
        product2 = new Product("P002", "Product 2", 200_000, 50);
        product3 = new Product("P003", "Product 3", 150_000, 200);
    }


    @Test
    void order_test() {
        // Given
        String userName = "testUser";

        OrderPaymentInfo orderPaymentInfo = new OrderPaymentInfo(PaymentMethod.CardType.KB, PaymentMethod.Type.CARD, 500_000);
        OrderProductInfo productInfo1 = new OrderProductInfo("P001", 3, 100_000);
        OrderProductInfo productInfo2 = new OrderProductInfo("P002", 1, 200_000);

        OrderRequestDto orderRequestDto = new OrderRequestDto(List.of(productInfo1, productInfo2), List.of(orderPaymentInfo));

        when(productRepository.findByCode("P001")).thenReturn(product1);
        when(productRepository.findByCode("P002")).thenReturn(product2);

        OrderItem orderItem1 = new OrderItem(product1, 3, 100_000);
        OrderItem orderItem2 = new OrderItem(product2, 1, 200_000);

        Order expectedOrder = new Order(any(), List.of(orderItem1, orderItem2), userName, OrderStatus.PAID, new ArrayList<>());
        Payment expectedPayment = new Payment(new PaymentMethod(PaymentMethod.Type.CARD, PaymentMethod.CardType.KB), expectedOrder, 500_000);
        expectedOrder.addPayment(expectedPayment);

        // When
        salesService.order(userName, orderRequestDto);

        // Then
        // 재고 차감 확인
        assertEquals(97, product1.getStock()); // 100 - 3
        assertEquals(49, product2.getStock()); // 50 - 1

        // product save 호출 여부 확인
        verify(productRepository).save(product1);
        verify(productRepository).save(product2);

        // order 주문 저장 확인
        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();
        assertEquals("testUser", savedOrder.getUserName());
        assertEquals(OrderStatus.PAID, savedOrder.getOrderStatus());
        assertEquals(2, savedOrder.getOrderItems().size());

        // 결제 저장 확인
        List<Payment> payments = savedOrder.getPayments();
        verify(payRepository).saveAll(payments);
    }

    @Test
    void cancelOrders_test() {
        // Given
        String userName = "testUser";

        OrderItem orderedItem1 = new OrderItem(product1, 3, 100_000);
        OrderItem orderedItem2 = new OrderItem(product2, 1, 200_000);

        OrderItem orderedItem3 = new OrderItem(product3, 1, 150_000);
        OrderItem orderedItem4 = new OrderItem(product1, 1, 100_000);

        PaymentMethod paidMethod1 = new PaymentMethod(PaymentMethod.Type.CARD, PaymentMethod.CardType.KB);
        PaymentMethod paidMethod2 = new PaymentMethod(PaymentMethod.Type.CASH, null);

        // 현금 + 카드 분할 결제 했던 주문
        Order expectedOrder1 = new Order(any(), List.of(orderedItem1, orderedItem2), userName, OrderStatus.PAID, new ArrayList<>());
        Payment expectedPayment1_1 = new Payment(paidMethod1, expectedOrder1, 200_000);
        Payment expectedPayment1_2 = new Payment(paidMethod2, expectedOrder1, 300_000);
        expectedOrder1.addPayment(expectedPayment1_1);
        expectedOrder1.addPayment(expectedPayment1_2);

        // 카드로 결제 했던 주문
        Order expectedOrder2 = new Order(any(), List.of(orderedItem3, orderedItem4), userName, OrderStatus.PAID, new ArrayList<>());
        Payment expectedPayment2_1 = new Payment(paidMethod1, expectedOrder2, 250_000);
        expectedOrder2.addPayment(expectedPayment2_1);

        when(orderRepository.findByUserNameWhereOrderStatus(userName, OrderStatus.PAID)).thenReturn(List.of(expectedOrder1, expectedOrder2));

        // When
        salesService.cancelOrders(userName);

        // Then
        // 재고 확인
        assertEquals(104, product1.getStock()); // 100 + 4
        assertEquals(51, product2.getStock()); // 50 + 1
        assertEquals(201, product3.getStock()); // 200 + 1

        // 주문 상태 확인 (PAID -> CANCELLED)
        assertEquals(OrderStatus.CANCELLED, expectedOrder1.getOrderStatus());
        assertEquals(OrderStatus.CANCELLED, expectedOrder2.getOrderStatus());

        // 주문 업데이트 적용 메서드 호출 확인
        verify(orderRepository).saveAll(List.of(expectedOrder1, expectedOrder2));
    }

    @Test
    void cancelOrder_test() {
        // Given
        String userName = "testUser";
        String orderCode = "orderCode";

        OrderItem orderedItem1 = new OrderItem(product1, 3, 100_000);
        OrderItem orderedItem2 = new OrderItem(product2, 1, 200_000);

        PaymentMethod paidMethod1 = new PaymentMethod(PaymentMethod.Type.CARD, PaymentMethod.CardType.KB);
        PaymentMethod paidMethod2 = new PaymentMethod(PaymentMethod.Type.CASH, null);

        Order expectedOrder1 = new Order(orderCode, List.of(orderedItem1, orderedItem2), userName, OrderStatus.PAID, new ArrayList<>());
        Payment expectedPayment1_1 = new Payment(paidMethod1, expectedOrder1, 200_000);
        Payment expectedPayment1_2 = new Payment(paidMethod2, expectedOrder1, 300_000);
        expectedOrder1.addPayment(expectedPayment1_1);
        expectedOrder1.addPayment(expectedPayment1_2);

        when(orderRepository.findByOrderCode(orderCode)).thenReturn(expectedOrder1);

        // When
        salesService.cancelOrder(orderCode);

        // Then
        // 재고 확인
        assertEquals(103, product1.getStock()); // 100 + 3
        assertEquals(51, product2.getStock()); // 50 + 1

        // 주문 상태 확인 (PAID -> CANCELLED)
        assertEquals(OrderStatus.CANCELLED, expectedOrder1.getOrderStatus());
    }
}