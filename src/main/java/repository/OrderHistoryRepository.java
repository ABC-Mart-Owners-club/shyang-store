package repository;

import domain.*;

import java.util.*;

public class OrderHistoryRepository {

    private Long sequence = 0L;
    static Map<Long, OrderHistory> orderHistoryMap = new HashMap<>();


    public OrderHistory save(OrderHistory orderHistory) {
        orderHistoryMap.put(orderHistory.getId(), orderHistory);
        sequence = sequence + 1L;
        return orderHistory;
    }

    public OrderHistory findById(Long id) {
        OrderHistory orderHistory = orderHistoryMap.get(id);
        if (orderHistory == null) {
            throw new IllegalArgumentException("주문 내역이 존재하지 않습니다.");
        }
        return orderHistory;
    }

    public List<OrderHistory> findByOrderGroupId(Long orderId) {
        return orderHistoryMap.values().stream()
                .filter(orderHistory -> orderHistory.getOrderGroupId().equals(orderId))
                .toList();
    }

    public List<OrderHistory> findByProductAndStatus(String productCode, Status status) {
        return orderHistoryMap.values().stream().filter(orderHistory ->
                orderHistory.getProductCode().equals(productCode) && orderHistory.getStatus().equals(status)).toList();

    }

    public List<OrderHistory> findAll() {
        return new ArrayList<>(orderHistoryMap.values());
    }

    public Long getSequence() {return sequence;}

}
