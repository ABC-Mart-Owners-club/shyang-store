package service;

import domain.*;
import dto.request.OrderProductRequestDto;
import repository.OrderGroupRepository;
import repository.OrderHistoryRepository;
import repository.ProductRepository;
import repository.UserRepository;

import java.util.List;

public class OrderService {

    private final OrderHistoryRepository orderHistoryRepository = new OrderHistoryRepository();
    private final OrderGroupRepository orderGroupRepository = new OrderGroupRepository();
    private final ProductRepository productRepository =  new ProductRepository();
    private final UserRepository userRepository = new UserRepository();


    // 주문 기능
    // 트랜잭션 처리 됐다고 가정 ~~
    public void orderProducts(List<OrderProductRequestDto> orderInfos, String userName) {

        OrderGroup orderGroup = orderGroupRepository.save(new OrderGroup(orderGroupRepository.getSequence()));
        User currentUser = userRepository.findByName(userName);

        for (OrderProductRequestDto orderInfo : orderInfos) {

            String productCode = orderInfo.getProductCode();
            Product product = productRepository.findByCode(productCode);

            int quantity = orderInfo.getQuantity();
            int price = product.getPrice();
            int amount = quantity * price;

            OrderHistory orderHistory = new OrderHistory(orderHistoryRepository.getSequence(), orderGroup.getId(), product.getCode(), quantity, price ,Status.PAID, currentUser.getName());
            orderHistoryRepository.save(orderHistory);

            product.deductStock(quantity);
            currentUser.chargeBalance(amount);
        }
    }

    // 전체 취소
    // 트랜잭션 처리 됐다고 가정 ~~
    public void cancelOrders(Long groupId) {

        List<OrderHistory> orders = orderHistoryRepository.findByOrderGroupId(groupId);
        for (OrderHistory order : orders) {
            cancelOrder(order);
        }
    }


    // 부분 취소
    // 트랜잭션 처리 됐다고 가정 ~~
    public void cancelOrder(Long orderHistoryId) {

        OrderHistory order = orderHistoryRepository.findById(orderHistoryId);
        cancelOrder(order);
    }

    // 상품별 판매 금액 조회
    public int getSalesByProduct(String productCode) {

        int result = 0;

        List<OrderHistory> orders = orderHistoryRepository.findByProductAndStatus(productCode, Status.PAID);

        for (OrderHistory order : orders) {
            int price = order.getPrice();
            int quantity = order.getQuantity();

            result += quantity * price;
        }
        return result;
    }


    private void cancelOrder(OrderHistory order) {

        Status status = order.getStatus();
        if (status == Status.CANCELLED) {throw new IllegalStateException("이미 취소된 주문입니다.");}

        Product product = productRepository.findByCode(order.getProductCode());
        User user = userRepository.findByName(order.getUserName());

        int orderQuantity = order.getQuantity();
        int orderPrice = order.getPrice();
        int amount = orderQuantity * orderPrice;


        order.cancelStatus();
        user.refundBalance(amount);
        product.refundStock(orderQuantity);
    }
}
