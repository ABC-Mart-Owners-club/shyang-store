package repository;

import domain.OrderGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderGroupRepository {

    private Long sequence = 0L;
    static Map<Long, OrderGroup> orderGroupMap = new HashMap<>();


    public OrderGroup save(OrderGroup orderGroup) {
        orderGroupMap.put(orderGroup.getId(), orderGroup);
        sequence = sequence + 1L;
        return orderGroup;
    }

    public List<OrderGroup> findAll() {
        return new ArrayList<>(orderGroupMap.values());
    }

    public Long getSequence() {return sequence;}

}
