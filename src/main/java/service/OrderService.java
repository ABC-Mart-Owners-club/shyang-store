package service;

import domain.*;
import dto.request.OrderProductRequestDto;
import dto.request.PayStatusRequestDto;
import repository.OrderGroupRepository;
import repository.OrderHistoryRepository;
import repository.ProductRepository;
import repository.impl.memory.*;

import java.util.List;

public class OrderService {

    private final OrderGroupRepository orderGroupRepository = new OrderGroupMemRepository();
    private final OrderHistoryRepository orderHistoryRepository = new OrderHistoryMemRepository();
    private final ProductRepository productRepository =  new ProductMemRepository();


    // 주문 기능
    // 트랜잭션 처리 됐다고 가정 ~~
    public void orderProducts(List<OrderProductRequestDto> orderInfos, String userName, PayStatusRequestDto payStatusRequest) {

        OrderGroup orderGroup = orderGroupRepository.save(new OrderGroup(MemPk.orderGroupPk));

        // 부분 결제
        if (payStatusRequest.isPartial()) {

            int cardAmount = payStatusRequest.getCardAmount();
            int cashAmount = payStatusRequest.getCashAmount();
            String carName = payStatusRequest.getCardName();

            int totalAmount = 0;
            for (OrderProductRequestDto orderInfo : orderInfos) {

                String productCode = orderInfo.getProductCode();
                Product product = productRepository.findByCode(productCode);

                int quantity = orderInfo.getQuantity();
                int price = product.getPrice();

                totalAmount += quantity * price;

                product.deductStock(quantity);

                OrderHistory orderHistory = new OrderHistory(MemPk.OrderHistoryPk, quantity, price ,Status.PARTIAL_PAID, userName, orderGroup.getId(), productCode, cashAmount, cardAmount, carName);
                orderHistoryRepository.save(orderHistory);
            }

            System.out.println(totalAmount);

            if (totalAmount != (cardAmount + cashAmount)) {throw new IllegalStateException("총 결제 금액이 맞지 않아요");}

        } else { // 부분 결제 X
            for (OrderProductRequestDto orderInfo : orderInfos) {

                Status status = null;
                String cardName = null;
                if(payStatusRequest.isFullCache()) {
                    status = Status.CACHE_PAID;
                } else {
                    status = Status.CARD_PAID;
                    cardName = payStatusRequest.getCardName();
                }

                String productCode = orderInfo.getProductCode();
                Product product = productRepository.findByCode(productCode);

                int quantity = orderInfo.getQuantity();
                int price = product.getPrice();

                product.deductStock(quantity);

                OrderHistory orderHistory = new OrderHistory(MemPk.OrderHistoryPk, quantity, price ,status, userName, orderGroup.getId(), productCode, null, null, cardName);
                orderHistoryRepository.save(orderHistory);
            }
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
        if(order.isPartial()){
            throw new IllegalStateException("분할 결제건은 부분 취소가 불가능합니다.");
        }
        cancelOrder(order);
    }

    // 상품별 판매 금액 조회
    public int getSalesByProduct(String productCode) {

        int result = 0;

        List<OrderHistory> orders = orderHistoryRepository.findByProductAndNotStatus(productCode, Status.CANCELLED);

        for (OrderHistory order : orders) {
            int price = order.getPrice();
            int quantity = order.getQuantity();

            result += quantity * price;
        }
        return result;
    }


    private void cancelOrder(OrderHistory order) {

        if (order.isCancelled()) {throw new IllegalStateException("이미 취소된 주문입니다.");}

        Product product = productRepository.findByCode(order.getProductCode());

        int orderQuantity = order.getQuantity();

        order.cancelStatus();
        product.refundStock(orderQuantity);
    }
}
