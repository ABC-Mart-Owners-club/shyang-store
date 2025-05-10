package order.repository;

import order.domain.Order;
import order.domain.OrderStatus;

import java.util.List;

public interface OrderRepository {
    public void save(Order order);
    public void saveAll(List<Order> orders);
    public Order findByOrderCode(String orderCode);
    public List<Order> findByUserNameWhereOrderStatus(String userName, OrderStatus orderStatus);
    public List<Order> findAllByNotOrderStatus(OrderStatus orderStatus);
    public List<Order> findAll();
}
