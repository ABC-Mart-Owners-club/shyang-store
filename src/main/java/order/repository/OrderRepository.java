package order.repository;

import order.domain.Order;

import java.util.List;

public interface OrderRepository {
    public Order save(Order order);
    public List<Order> findAll();
}
