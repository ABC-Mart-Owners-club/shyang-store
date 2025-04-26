package repository;

import domain.OrderGroup;

import java.util.ArrayList;
import java.util.List;

public interface OrderGroupRepository {
    public OrderGroup save(OrderGroup orderGroup);
    public List<OrderGroup> findAll();

}
