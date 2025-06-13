//package service;
//
//import dto.response.ProductSalesReport;
//import order.domain.Order;
//import order.domain.OrderItem;
//import order.domain.OrderStatus;
//import order.repository.OrderRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//import pay.domain.Payment;
//import pay.domain.PaymentMethod;
//import pay.repository.PayRepository;
//import product.domain.Product;
//
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class AdminServiceTest {
//
//    @Mock
//    private OrderRepository orderRepository;
//    @Mock
//    private PayRepository payRepository;
//    @InjectMocks
//    private AdminService adminService;
//
//    private Product product1;
//    private Product product2;
//    private Product product3;
//
//    @BeforeEach
//    public void setUp() {
//        // 상품 초기화
//        product1 = new Product("P001", "Product 1", 100_000, 100);
//        product2 = new Product("P002", "Product 2", 200_000, 50);
//        product3 = new Product("P003", "Product 3", 150_000, 200);
//    }
//
//    // 상품 판매 금액 조회 테스트
//    @Test
//    void getProductSalesReport_test() {
//
//        // Given
//        String userName = "testUser";
//        String orderCode1 = "ORD001"; // 임의 주문 코드
//        String orderCode2 = "ORD002"; // 임의 주문 코드
//
//        OrderItem orderedItem1 = new OrderItem(product1, 3, 100_000);
//        OrderItem orderedItem2 = new OrderItem(product2, 1, 200_000);
//
//        OrderItem orderedItem3 = new OrderItem(product3, 1, 150_000);
//        OrderItem orderedItem4 = new OrderItem(product1, 1, 100_000);
//
//        PaymentMethod paidMethod1 = new PaymentMethod(PaymentMethod.Type.CARD, PaymentMethod.CardType.KB);
//        PaymentMethod paidMethod2 = new PaymentMethod(PaymentMethod.Type.CASH, null);
//
//        // 현금 + 카드 분할 결제 했던 주문
//        Order expectedOrder1 = new Order(orderCode1, List.of(orderedItem1, orderedItem2), userName, OrderStatus.PAID, new ArrayList<>());
//        Payment expectedPayment1_1 = new Payment(paidMethod1, expectedOrder1, 250_000);
//        Payment expectedPayment1_2 = new Payment(paidMethod2, expectedOrder1, 250_000);
//        expectedOrder1.addPayment(expectedPayment1_1);
//        expectedOrder1.addPayment(expectedPayment1_2);
//
//        // 카드로 결제 했던 주문
//        Order expectedOrder2 = new Order(orderCode2, List.of(orderedItem3, orderedItem4), userName, OrderStatus.PAID, new ArrayList<>());
//        Payment expectedPayment2_1 = new Payment(paidMethod1, expectedOrder2, 250_000);
//        expectedOrder2.addPayment(expectedPayment2_1);
//
//        when(orderRepository.findAllByNotOrderStatus(OrderStatus.CANCELLED)).thenReturn(List.of(expectedOrder1, expectedOrder2));
//
//        // when
//        List<ProductSalesReport> reports = adminService.getProductSalesReport();
//        Map<String, Integer> reportMap = reports.stream()
//                .collect(Collectors.toMap(
//                        ProductSalesReport::getProductCode,
//                        ProductSalesReport::getTotalSalesAmount
//                ));
//
//
//        // then
//        assertEquals(400_000, reportMap.get("P001"));
//        assertEquals(200_000, reportMap.get("P002"));
//        assertEquals(150_000, reportMap.get("P003"));
//    }
//
//    // 카드사별 판매 금액 조회 테스트
//    @Test
//    void testGetCardSalesReport() {
//        // Given
//        String userName = "testUser";
//        String orderCode1 = "ORD001"; // 임의 주문 코드
//        String orderCode2 = "ORD002"; // 임의 주문 코드
//        String orderCode3 = "ORD003"; // 임의 주문 코드
//
//        OrderItem orderedItem1 = new OrderItem(product1, 3, 100_000);
//        OrderItem orderedItem2 = new OrderItem(product2, 1, 200_000);
//
//        OrderItem orderedItem3 = new OrderItem(product3, 1, 150_000);
//        OrderItem orderedItem4 = new OrderItem(product1, 1, 100_000);
//
//        OrderItem orderedItem5 = new OrderItem(product1, 3, 100_000);
//        OrderItem orderedItem6 = new OrderItem(product2, 10, 200_000);
//        OrderItem orderedItem7 = new OrderItem(product3, 5, 150_000);
//
//        PaymentMethod paidMethod1 = new PaymentMethod(PaymentMethod.Type.CARD, PaymentMethod.CardType.KB);
//        PaymentMethod paidMethod2 = new PaymentMethod(PaymentMethod.Type.CASH, null);
//        PaymentMethod paidMethod3 = new PaymentMethod(PaymentMethod.Type.CARD, PaymentMethod.CardType.HANA);
//        PaymentMethod paidMethod4 = new PaymentMethod(PaymentMethod.Type.CARD, PaymentMethod.CardType.SHIN_HAN);
//
//        // 현금 + 카드 분할 결제 했던 주문
//        Order expectedOrder1 = new Order(orderCode1, List.of(orderedItem1, orderedItem2), userName, OrderStatus.PAID, new ArrayList<>());
//        Payment expectedPayment1_1 = new Payment(paidMethod1, expectedOrder1, 250_000);
//        Payment expectedPayment1_2 = new Payment(paidMethod2, expectedOrder1, 250_000);
//        expectedOrder1.addPayment(expectedPayment1_1);
//        expectedOrder1.addPayment(expectedPayment1_2);
//
//        // 카드로 결제 했던 주문
//        Order expectedOrder2 = new Order(orderCode2, List.of(orderedItem3, orderedItem4), userName, OrderStatus.PAID, new ArrayList<>());
//        Payment expectedPayment2_1 = new Payment(paidMethod1, expectedOrder2, 250_000);
//        expectedOrder2.addPayment(expectedPayment2_1);
//
//
//        // 카드로 분할 결제
//        Order expectedOrder3 = new Order(orderCode3, List.of(orderedItem5, orderedItem6, orderedItem7), userName, OrderStatus.PAID, new ArrayList<>());
//        Payment expectedPayment3_1 = new Payment(paidMethod1, expectedOrder3, 2_000_000);
//        Payment expectedPayment3_2 = new Payment(paidMethod3, expectedOrder3, 900_000);
//        Payment expectedPayment3_3 = new Payment(paidMethod4, expectedOrder3, 150_000);
//        expectedOrder3.addPayment(expectedPayment3_1);
//        expectedOrder3.addPayment(expectedPayment3_2);
//        expectedOrder3.addPayment(expectedPayment3_3);
//
//        when(payRepository.findALLByPaymentType(PaymentMethod.Type.CARD)).thenReturn(List.of(expectedPayment3_1, expectedPayment3_2, expectedPayment3_3));
//
//        // when
//        Map<String, Integer> cardSalesReport = adminService.getCardSalesReport();
//
//        // then
//        assertEquals(2_000_000, cardSalesReport.get("KB"));
//        assertEquals(900_000, cardSalesReport.get("HANA"));
//        assertEquals(150_000, cardSalesReport.get("SHIN_HAN"));
//
//    }
//}