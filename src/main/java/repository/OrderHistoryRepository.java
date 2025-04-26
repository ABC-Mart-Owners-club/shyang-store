package repository;

import domain.OrderHistory;
import domain.Status;

import java.util.ArrayList;
import java.util.List;

public interface OrderHistoryRepository {

    public OrderHistory save(OrderHistory orderHistory);

    public OrderHistory findById(Long id);

    public List<OrderHistory> findByOrderGroupId(Long orderId);

    public List<OrderHistory> findByProductAndStatus(String productCode, Status status);

    public List<OrderHistory> findByProductAndNotStatus(String productCode, Status status);

    public List<OrderHistory> findByCardNameAndStatus(String cardName, Status status);

    public List<OrderHistory> findAll();

}
