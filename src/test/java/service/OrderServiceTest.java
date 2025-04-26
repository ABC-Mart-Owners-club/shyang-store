package service;

import domain.OrderHistory;
import domain.Product;
import dto.request.OrderProductRequestDto;
import dto.request.PayStatusRequestDto;
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

    @BeforeEach
    void setUp() {
        orderService = new OrderService();
    }

    @Test
    void orderProducts_success() {
        // given
        OrderProductRequestDto request1 = new OrderProductRequestDto("CODE-1", 2);
        OrderProductRequestDto request2 = new OrderProductRequestDto("CODE-2", 1);
        PayStatusRequestDto cashAll = new PayStatusRequestDto(null, null, null);

        // when
        orderService.orderProducts(List.of(request1, request2), "Simon", cashAll);

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
        PayStatusRequestDto cashAll = new PayStatusRequestDto(null, null, null);

        orderService.orderProducts(List.of(request, request2), "Simon", cashAll);

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
        PayStatusRequestDto cashAll = new PayStatusRequestDto(null, null, null);

        // 주문
        orderService.orderProducts(List.of(request, request2), "Simon", cashAll);

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
        PayStatusRequestDto cashAll = new PayStatusRequestDto(null, null, null);

        OrderProductRequestDto request1 = new OrderProductRequestDto("CODE-1", 5);
        OrderProductRequestDto request2 = new OrderProductRequestDto("CODE-2", 3);
        orderService.orderProducts(List.of( request1, request2), "Simon", cashAll);


        OrderProductRequestDto request3 = new OrderProductRequestDto("CODE-1", 2);
        OrderProductRequestDto request4 = new OrderProductRequestDto("CODE-3", 10);
        orderService.orderProducts(List.of( request3, request4), "Potter", cashAll);


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
        PayStatusRequestDto cashAll = new PayStatusRequestDto(null, null, null);
        orderService.orderProducts(List.of(request), "Simon", cashAll);
        Long orderHistoryId = 0L;

        orderService.cancelOrder(orderHistoryId);

        // when & then
        IllegalStateException e = assertThrows(IllegalStateException.class, () -> {
            orderService.cancelOrder(orderHistoryId);
        });

        assertEquals("이미 취소된 주문입니다.", e.getMessage());
    }


    @Test
    void orderProducts_card_success() {
        // given
        OrderProductRequestDto request1 = new OrderProductRequestDto("CODE-1", 2);
        OrderProductRequestDto request2 = new OrderProductRequestDto("CODE-2", 1);
        PayStatusRequestDto cardAll = new PayStatusRequestDto(null, null, "신한");

        // when
        orderService.orderProducts(List.of(request1, request2), "Simon", cardAll);

        // then
        Product product1 = productRepository.findByCode("CODE-1");
        Product product2 = productRepository.findByCode("CODE-2");

        assertEquals(498, product1.getStock()); // 500 - 2
        assertEquals(199, product2.getStock()); // 200 - 1
    }

    @Test
    void orderProducts_partial_success() {
        // given
        OrderProductRequestDto request1 = new OrderProductRequestDto("CODE-1", 2);
        OrderProductRequestDto request2 = new OrderProductRequestDto("CODE-2", 1);
        PayStatusRequestDto cardAll = new PayStatusRequestDto(300_000, 20_000, "신한");

        // when
        orderService.orderProducts(List.of(request1, request2), "Simon", cardAll);

        // then
        Product product1 = productRepository.findByCode("CODE-1");
        Product product2 = productRepository.findByCode("CODE-2");

        assertEquals(498, product1.getStock()); // 500 - 2
        assertEquals(199, product2.getStock()); // 200 - 1
    }

    @Test
    void cancelOrders_partial_success() {
        // given
        OrderProductRequestDto request = new OrderProductRequestDto("CODE-1", 2);
        OrderProductRequestDto request2 = new OrderProductRequestDto("CODE-2", 1);
        PayStatusRequestDto partial = new PayStatusRequestDto(300_000, 20_000, "신한");

        orderService.orderProducts(List.of(request, request2), "Simon", partial);

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
    void getTotalByCard_success() {
        // given
        OrderProductRequestDto request1 = new OrderProductRequestDto("CODE-1", 2);
        OrderProductRequestDto request2 = new OrderProductRequestDto("CODE-2", 1);
        PayStatusRequestDto cardAll = new PayStatusRequestDto(null, null, "신한");
        orderService.orderProducts(List.of(request1, request2), "Simon", cardAll);


        OrderProductRequestDto request3 = new OrderProductRequestDto("CODE-1", 3);
        OrderProductRequestDto request4 = new OrderProductRequestDto("CODE-2", 3);
        PayStatusRequestDto cashAll = new PayStatusRequestDto(null, null, null);
        orderService.orderProducts(List.of(request3, request4), "Simon", cashAll);


        OrderProductRequestDto request5 = new OrderProductRequestDto("CODE-1", 10);
        OrderProductRequestDto request6 = new OrderProductRequestDto("CODE-2", 20);
        PayStatusRequestDto partial = new PayStatusRequestDto(3_000_000, 400_000, "국민");
        orderService.orderProducts(List.of(request5, request6), "Simon", partial);


        OrderProductRequestDto request7 = new OrderProductRequestDto("CODE-1", 10);
        OrderProductRequestDto request8 = new OrderProductRequestDto("CODE-3", 20);
        PayStatusRequestDto partial2 = new PayStatusRequestDto(1_000_000, 1_800_000, "하나");
        orderService.orderProducts(List.of(request7, request8), "Simon", partial2);


        Long orderGroupId = 0L; // MemPk.OrderHistoryPk가 0부터 시작한다고 가정
        List<OrderHistory> orderHistoryList = orderHistoryRepository.findByOrderGroupId(orderGroupId);
        Long orderHistoryId = orderHistoryList.stream()
                .filter(orderHistory -> orderHistory.getProductCode().equals("CODE-1"))
                .toList().get(0).getId();

        orderService.cancelOrder(orderHistoryId);

        // when
        int card1 = orderService.getSalesByCard("하나");
        int card2 = orderService.getSalesByCard("신한");
        int card3 = orderService.getSalesByCard("국민");


        // then
        assertEquals(1_800_000, card1);
        assertEquals(120_000, card2);
        assertEquals(400_000, card3);

    }

}