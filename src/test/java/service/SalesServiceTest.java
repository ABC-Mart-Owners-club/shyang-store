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

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

    private Product product1_1;
    private Product product1_2;
    private Product product2_1;
    private Product product2_2;
    private Product product3;

    @BeforeEach
    public void setUp() {
        // 상품 초기화
        LocalDate now = LocalDate.now();

        product1_1 = new Product("P001", now.minusDays(1) , "Product 1", 100_000, 100);
        product1_2 = new Product("P001", now.minusDays(40) , "Product 1", 100_000, 2);

        product2_1 = new Product("P002", now.minusDays(1) , "Product 2", 200_000, 50);
        product2_2 = new Product("P002", now.minusDays(8) , "Product 2", 200_000, 1);

        product3 = new Product("P003", now.minusDays(8) , "Product 3", 150_000, 50);
    }



    @Test
    void getTotalPrice_test() {

        // Given
        OrderProductInfo productInfo1 = new OrderProductInfo("P001", 3, 100_000);
        OrderProductInfo productInfo2 = new OrderProductInfo("P002", 2, 200_000);
        OrderProductInfo productInfo3 = new OrderProductInfo("P003", 5, 150_000);
        List<OrderProductInfo> orderProductInfos = List.of(productInfo1, productInfo2, productInfo3);

        when(productRepository.countTotalStock("P001")).thenReturn(10);
        when(productRepository.countTotalStock("P002")).thenReturn(10);
        when(productRepository.countTotalStock("P003")).thenReturn(10);

        when(productRepository.findByCodeOrderByReceiveDate("P001")).thenReturn(List.of(product1_2, product1_1));
        when(productRepository.findByCodeOrderByReceiveDate("P002")).thenReturn(List.of(product2_2, product2_1));
        when(productRepository.findByCodeOrderByReceiveDate("P003")).thenReturn(List.of(product3));

        // When
        int totalPrice = salesService.getTotalPrice(orderProductInfos);

        // Then
        // P001 : 100_000 x 50% x 2 + 100_000 x 1 = 200_000
        // P001 : 200_000 x 70% x 1 + 200_000 x 1 = 340_000
        // P003 : 150_000 x 70% x 5 = 525_000
        // 합계 : 1_065_000
        assertEquals(1_065_000, totalPrice);
    }

    @Test
    void order_test() {
        // Given
        String userName = "testUser";

        OrderPaymentInfo orderPaymentInfo = new OrderPaymentInfo(PaymentMethod.CardType.KB, PaymentMethod.Type.CARD, 340_000);
        OrderProductInfo productInfo1 = new OrderProductInfo("P001", 3, 100_000);
        OrderProductInfo productInfo2 = new OrderProductInfo("P002", 1, 200_000);

        OrderRequestDto orderRequestDto = new OrderRequestDto(List.of(productInfo1, productInfo2), List.of(orderPaymentInfo));


        when(productRepository.countTotalStock("P001")).thenReturn(102);
        when(productRepository.countTotalStock("P002")).thenReturn(51);
        when(productRepository.findByCodeOrderByReceiveDate("P001")).thenReturn(List.of(product1_2, product1_1));
        when(productRepository.findByCodeOrderByReceiveDate("P002")).thenReturn(List.of(product2_2, product2_1));

        OrderItem orderItem1_1 = new OrderItem(product1_1, 1, 100_000);
        OrderItem orderItem1_2 = new OrderItem(product1_2, 2, 50_000);
        OrderItem orderItem2_2 = new OrderItem(product2_2, 1, 140_000);

        Order expectedOrder = new Order(any(), List.of(orderItem1_1, orderItem1_2, orderItem2_2), userName, OrderStatus.PAID, new ArrayList<>());
        Payment expectedPayment = new Payment(new PaymentMethod(PaymentMethod.Type.CARD, PaymentMethod.CardType.KB), expectedOrder, 340_000);
        expectedOrder.addPayment(expectedPayment);

        // When
        salesService.order(userName, orderRequestDto);

        // Then
        // 재고 차감 확인
        assertEquals(99, product1_1.getStock()); // 100 - 1
        assertEquals(0, product1_2.getStock()); // 2 - 2
        assertEquals(0, product2_2.getStock()); // 1 - 1

        // product save 호출 여부 확인
        verify(productRepository).save(product1_1);
        verify(productRepository).save(product1_2);
        verify(productRepository).save(product2_2);

        // order 주문 저장 확인
        verify(orderRepository).save(orderCaptor.capture());
        Order savedOrder = orderCaptor.getValue();
        assertEquals("testUser", savedOrder.getUserName());
        assertEquals(OrderStatus.PAID, savedOrder.getOrderStatus());
        assertEquals(3, savedOrder.getOrderItems().size());

        // 결제 저장 확인
        List<Payment> payments = savedOrder.getPayments();
        verify(payRepository).saveAll(payments);
    }

    @Test
    void cancelOrders_test() {
        // Given
        String userName = "testUser";

        OrderItem orderedItem1_1 = new OrderItem(product1_1, 1, 100_000);
        OrderItem orderedItem1_2 = new OrderItem(product1_2, 2, 50_000);
        OrderItem orderedItem2_2 = new OrderItem(product2_2, 1, 140_000);

        OrderItem orderedItemSecond3 = new OrderItem(product3, 1, 105_000);
        OrderItem orderedItemSecond1_1 = new OrderItem(product1_1, 1, 100_000);


        PaymentMethod paidMethod1 = new PaymentMethod(PaymentMethod.Type.CARD, PaymentMethod.CardType.KB);
        PaymentMethod paidMethod2 = new PaymentMethod(PaymentMethod.Type.CASH, null);

        // 현금 + 카드 분할 결제 했던 주문
        Order expectedOrder1 = new Order(any(), List.of(orderedItem1_1, orderedItem1_2, orderedItem2_2), userName, OrderStatus.PAID, new ArrayList<>());
        Payment expectedPayment1_1 = new Payment(paidMethod1, expectedOrder1, 40_000);
        Payment expectedPayment1_2 = new Payment(paidMethod2, expectedOrder1, 300_000);
        expectedOrder1.addPayment(expectedPayment1_1);
        expectedOrder1.addPayment(expectedPayment1_2);

        // 카드로 결제 했던 주문
        Order expectedOrder2 = new Order(any(), List.of(orderedItemSecond3, orderedItemSecond1_1), userName, OrderStatus.PAID, new ArrayList<>());
        Payment expectedPayment2_1 = new Payment(paidMethod1, expectedOrder2, 205_000);
        expectedOrder2.addPayment(expectedPayment2_1);

        when(orderRepository.findByUserNameWhereOrderStatus(userName, OrderStatus.PAID)).thenReturn(List.of(expectedOrder1, expectedOrder2));

        // When
        salesService.cancelOrders(userName);

        // Then
        // 재고 확인
        assertEquals(102, product1_1.getStock()); // 100 + 2
        assertEquals(4, product1_2.getStock()); // 2 + 2
        assertEquals(2, product2_2.getStock()); // 1 + 1
        assertEquals(51, product3.getStock()); // 50 + 1

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

        OrderItem orderedItem1_1 = new OrderItem(product1_1, 1, 100_000);
        OrderItem orderedItem1_2 = new OrderItem(product1_2, 2, 50_000);
        OrderItem orderedItem2_2 = new OrderItem(product2_2, 1, 140_000);

        PaymentMethod paidMethod1 = new PaymentMethod(PaymentMethod.Type.CARD, PaymentMethod.CardType.KB);
        PaymentMethod paidMethod2 = new PaymentMethod(PaymentMethod.Type.CASH, null);

        // 현금 + 카드 분할 결제 했던 주문
        Order expectedOrder1 = new Order(any(), List.of(orderedItem1_1, orderedItem1_2, orderedItem2_2), userName, OrderStatus.PAID, new ArrayList<>());
        Payment expectedPayment1_1 = new Payment(paidMethod1, expectedOrder1, 40_000);
        Payment expectedPayment1_2 = new Payment(paidMethod2, expectedOrder1, 300_000);
        expectedOrder1.addPayment(expectedPayment1_1);
        expectedOrder1.addPayment(expectedPayment1_2);

        when(orderRepository.findByOrderCode(orderCode)).thenReturn(expectedOrder1);

        // When
        salesService.cancelOrder(orderCode);

        // Then
        // 재고 확인
        assertEquals(101, product1_1.getStock()); // 100 + 1
        assertEquals(4, product1_2.getStock()); // 2 + 2
        assertEquals(2, product2_2.getStock()); // 1 + 1

        // 주문 상태 확인 (PAID -> CANCELLED)
        assertEquals(OrderStatus.CANCELLED, expectedOrder1.getOrderStatus());
    }
}