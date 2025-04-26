package repository.impl.memory;

import domain.OrderGroup;
import repository.OrderGroupRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderGroupMemRepository implements OrderGroupRepository {

    static Map<Long, OrderGroup> orderGroupMap = new HashMap<>();


    @Override
    public OrderGroup save(OrderGroup orderGroup) {
        orderGroupMap.put(orderGroup.getId(), orderGroup);
        MemPk.orderGroupPk += 1;
        return orderGroup;
    }

    @Override
    public List<OrderGroup> findAll() {
        return new ArrayList<>(orderGroupMap.values());
    }


}
