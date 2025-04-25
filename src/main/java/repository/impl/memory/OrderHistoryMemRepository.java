package repository.impl.memory;

import domain.*;
import repository.OrderHistoryRepository;

import java.util.*;

public class OrderHistoryMemRepository implements OrderHistoryRepository {

    private Long sequence = 0L;
    static Map<Long, OrderHistory> orderHistoryMap = new HashMap<>();


    @Override
    public OrderHistory save(OrderHistory orderHistory) {
        orderHistoryMap.put(orderHistory.getId(), orderHistory);
        sequence = sequence + 1L;
        return orderHistory;
    }

    @Override
    public OrderHistory findById(Long id) {
        OrderHistory orderHistory = orderHistoryMap.get(id);
        if (orderHistory == null) {
            throw new IllegalArgumentException("주문 내역이 존재하지 않습니다.");
        }
        return orderHistory;
    }

    @Override
    public List<OrderHistory> findByOrderGroupId(Long orderId) {
        return orderHistoryMap.values().stream()
                .filter(orderHistory -> orderHistory.getOrderGroup().getId().equals(orderId))
                .toList();
    }

    @Override
    public List<OrderHistory> findByProductAndStatus(String productCode, Status status) {
        return orderHistoryMap.values().stream().filter(orderHistory ->
                orderHistory.getProduct().getCode().equals(productCode) && orderHistory.getStatus().equals(status)).toList();

    }

    @Override
    public List<OrderHistory> findAll() {
        return new ArrayList<>(orderHistoryMap.values());
    }

    public Long getSequence() {return sequence;}

}
