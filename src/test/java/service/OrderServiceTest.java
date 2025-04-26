package service;

import domain.OrderHistory;
import domain.Product;
import dto.request.OrderProductRequestDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import repository.OrderGroupRepository;
import repository.OrderHistoryRepository;
import repository.ProductRepository;
import repository.impl.memory.OrderGroupMemRepository;
import repository.impl.memory.OrderHistoryMemRepository;
import repository.impl.memory.ProductMemRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {

    private OrderService orderService;

    private ProductRepository productRepository = new ProductMemRepository();
    private OrderHistoryRepository orderHistoryRepository = new OrderHistoryMemRepository();
    private OrderGroupRepository orderGroupRepository = new OrderGroupMemRepository();

    @BeforeEach
    void setUp() {
        orderService = new OrderService();
    }

    @Test
    void orderProducts_success() {
        // given
        OrderProductRequestDto request1 = new OrderProductRequestDto("CODE-1", 2);
        OrderProductRequestDto request2 = new OrderProductRequestDto("CODE-2", 1);

        // when
        orderService.orderProducts(List.of(request1, request2), "Simon");

        // then
        Product product1 = productRepository.findByCode("CODE-1");
        Product product2 = productRepository.findByCode("CODE-2");

        assertEquals(498, product1.getStock()); // 500 - 2
        assertEquals(199, product2.getStock()); // 200 - 1
    }

    @Test
    void cancelOrders_success() {
        // given
        OrderProductRequestDto request = new OrderProductRequestDto("CODE-1", 5);
        OrderProductRequestDto request2 = new OrderProductRequestDto("CODE-2", 3);

        orderService.orderProducts(List.of(request, request2), "Simon");

        Long orderGroupId = 0L; // MemPk.orderGroupPk가 0부터 시작하니까 ~~

        // when
        orderService.cancelOrders(orderGroupId);

        // then
        Product product1 = productRepository.findByCode("CODE-1");
        Product product2 = productRepository.findByCode("CODE-2");

        assertEquals(500, product1.getStock()); // 재고가 복구되어야 함
        assertEquals(200, product2.getStock()); // 재고가 복구되어야 함
    }

    @Test
    void cancelOrder_success() {
        // given
        OrderProductRequestDto request = new OrderProductRequestDto("CODE-1", 5);
        OrderProductRequestDto request2 = new OrderProductRequestDto("CODE-2", 3);

        // 주문
        orderService.orderProducts(List.of(request, request2), "Simon");

        Long orderGroupId = 0L; // MemPk.OrderHistoryPk가 0부터 시작한다고 가정
        List<OrderHistory> orderHistoryList = orderHistoryRepository.findByOrderGroupId(orderGroupId);
        Long orderHistoryId = orderHistoryList.stream()
                .filter(orderHistory -> orderHistory.getProductCode().equals("CODE-1"))
                .toList().get(0).getId();


        // when
        // 부분 취소
        orderService.cancelOrder(orderHistoryId);

        // then
        Product product1 = productRepository.findByCode("CODE-1");
        Product product2 = productRepository.findByCode("CODE-2");

        assertEquals(500, product1.getStock()); // 부분 취소한 재고 복구
        assertEquals(197, product2.getStock());
    }

    @Test
    void getSalesByProduct_success() {
        // given

        OrderProductRequestDto request1 = new OrderProductRequestDto("CODE-1", 5);
        OrderProductRequestDto request2 = new OrderProductRequestDto("CODE-2", 3);
        orderService.orderProducts(List.of( request1, request2), "Simon");


        OrderProductRequestDto request3 = new OrderProductRequestDto("CODE-1", 2);
        OrderProductRequestDto request4 = new OrderProductRequestDto("CODE-3", 10);
        orderService.orderProducts(List.of( request3, request4), "Potter");


        Long orderGroupId = 0L; // MemPk.OrderHistoryPk가 0부터 시작한다고 가정
        List<OrderHistory> orderHistoryList = orderHistoryRepository.findByOrderGroupId(orderGroupId);
        Long orderHistoryId = orderHistoryList.stream()
                .filter(orderHistory -> orderHistory.getProductCode().equals("CODE-1"))
                .toList().get(0).getId();

        orderService.cancelOrder(orderHistoryId);


        // when
        int sales1 = orderService.getSalesByProduct("CODE-1");
        int sales2 = orderService.getSalesByProduct("CODE-2");
        int sales3 = orderService.getSalesByProduct("CODE-3");

        // then
        assertEquals(200_000, sales1);
        assertEquals(360_000, sales2);
        assertEquals(900_000, sales3);
    }

    @Test
    void cancelOrder_alreadyCancelled() {
        // given
        OrderProductRequestDto request = new OrderProductRequestDto("CODE-1", 1);
        orderService.orderProducts(List.of(request), "Simon");
        Long orderHistoryId = 0L;

        orderService.cancelOrder(orderHistoryId);

        // when & then
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
            orderService.cancelOrder(orderHistoryId);
        });

        assertEquals("이미 취소된 주문입니다.", e.getMessage());
    }
}