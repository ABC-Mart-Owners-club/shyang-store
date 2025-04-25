package repository.impl.memory;

import domain.OrderGroup;
import repository.OrderGroupRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderGroupMemRepository implements OrderGroupRepository {

    private Long sequence = 0L;
    static Map<Long, OrderGroup> orderGroupMap = new HashMap<>();


    @Override
    public OrderGroup save(OrderGroup orderGroup) {
        orderGroupMap.put(orderGroup.getId(), orderGroup);
        sequence = sequence + 1L;
        return orderGroup;
    }

    @Override
    public List<OrderGroup> findAll() {
        return new ArrayList<>(orderGroupMap.values());
    }

    public Long getSequence() {
        return sequence;
    }

}
