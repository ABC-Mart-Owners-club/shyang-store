import domain.OrderHistory;
import domain.Product;
import domain.User;
import dto.request.OrderProductRequestDto;
import repository.impl.memory.OrderHistoryMemRepository;
import repository.impl.memory.ProductMemRepository;
import repository.impl.memory.UserMemRepository;
import service.OrderService;

import java.util.List;

public class ShoeMain {

    public static void main(String[] args) {


        OrderService orderService = new OrderService();


        List<OrderProductRequestDto> orderInfos = List.of(
                new OrderProductRequestDto("CODE-1", 3),
                new OrderProductRequestDto("CODE-2", 2)

        );

        orderService.orderProducts(orderInfos, "Simon");

        UserMemRepository userRepository = new UserMemRepository();
        ProductMemRepository productRepository = new ProductMemRepository();

        User byName = userRepository.findByName("Simon");
        Product product1 = productRepository.findByCode("CODE-1");
        Product product2 = productRepository.findByCode("CODE-2");

        System.out.println(byName);
        System.out.println(product1);
        System.out.println(product2);

        int result1 = orderService.getSalesByProduct("CODE-1");
        int result2 = orderService.getSalesByProduct("CODE-2");
        int result3 = orderService.getSalesByProduct("CODE-3");
        int result4 = orderService.getSalesByProduct("CODE-4");
        int result5 = orderService.getSalesByProduct("CODE-5");

        System.out.println(result1);
        System.out.println(result2);
        System.out.println(result3);
        System.out.println(result4);
        System.out.println(result5);


        OrderHistoryMemRepository orderHistoryRepository = new OrderHistoryMemRepository();
        List<OrderHistory> orders = orderHistoryRepository.findAll();
        List<OrderHistory> cancelOrders = orders.stream().filter(order -> order.getUserName().equals("Simon")).toList();

        orderService.cancelOrders(cancelOrders.get(0).getOrderGroupId());
        System.out.println(byName);
        System.out.println(product1);
        System.out.println(product2);

        int result6 = orderService.getSalesByProduct("CODE-1");
        int result7 = orderService.getSalesByProduct("CODE-2");
        int result8 = orderService.getSalesByProduct("CODE-3");
        int result9 = orderService.getSalesByProduct("CODE-4");
        int result10 = orderService.getSalesByProduct("CODE-5");

        System.out.println(result6);
        System.out.println(result7);
        System.out.println(result8);
        System.out.println(result9);
        System.out.println(result10);




        orderService.orderProducts(orderInfos, "Simon");

        User byName2 = userRepository.findByName("Simon");
        Product product3 = productRepository.findByCode("CODE-1");
        Product product4 = productRepository.findByCode("CODE-2");

        System.out.println(byName2);
        System.out.println(product3);
        System.out.println(product4);

        List<OrderHistory> orders2 = orderHistoryRepository.findAll();
        List<OrderHistory> cancelOrders2 = orders2.stream().filter(order -> order.getUserName().equals("Simon")).toList();

        orderService.cancelOrder(3L);

        System.out.println(byName2);
        System.out.println(product3);
        System.out.println(product4);

        List<OrderHistory> all = orderHistoryRepository.findAll();
        System.out.println(all);


        int result11 = orderService.getSalesByProduct("CODE-1");
        int result12 = orderService.getSalesByProduct("CODE-2");
        int result13 = orderService.getSalesByProduct("CODE-3");
        int result14 = orderService.getSalesByProduct("CODE-4");
        int result15 = orderService.getSalesByProduct("CODE-5");

        System.out.println(result11);
        System.out.println(result12);
        System.out.println(result13);
        System.out.println(result14);
        System.out.println(result15);
    }
}
