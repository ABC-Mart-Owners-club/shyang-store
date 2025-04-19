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
        return orderHistoryMap.get(id);
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
