package repository;

import domain.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderGroupRepository {

    private Long sequence = 0L;
    static Map<Long, OrderGroup> orderGroupMap = new HashMap<>();

    public OrderGroup findById(Long id) {
        return orderGroupMap.get(id);
    }

    public List<OrderGroup> findByOrderHistory(OrderHistory orderHistory) {
        return orderGroupMap.values().stream()
                .filter(orderGroup -> orderGroup.getOrderHistory().getId().equals(orderHistory.getId()))
                .toList();
    }

    public List<OrderGroup> findByOUser(User user) {
        return orderGroupMap.values().stream()
                .filter(orderGroup -> orderGroup.getUser().getId().equals(user.getId()))
                .toList();
    }

    public Long save(OrderHistory orderHistory, User user) {
        sequence ++;
        OrderGroup orderGroup = new OrderGroup(sequence, orderHistory, user);
        orderGroupMap.put(sequence, orderGroup);
        return sequence;
    }

    public Long getSequence() {
        return sequence;
    }
}
