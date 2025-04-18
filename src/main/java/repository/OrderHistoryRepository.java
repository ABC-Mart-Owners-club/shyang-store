package repository;

import domain.OrderHistory;
import domain.OrderType;
import domain.Product;
import domain.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderHistoryRepository {

    private Long sequence = 0L;
    static Map<Long, OrderHistory> historyMap = new HashMap<>();

    public OrderHistory findById(Long id) {
        return historyMap.get(id);
    }

    public List<OrderHistory> findByProduct(Product product) {
        return historyMap.values().stream()
                .filter(orderHistory -> orderHistory.getProduct().getId().equals(product.getId()))
                .toList();
    }

    public Long save(Product product, int quantity, int price, OrderType orderType) {
        sequence ++;
        OrderHistory orderHistory = new OrderHistory(sequence, product, quantity, price, orderType);
        historyMap.put(sequence, orderHistory);
        return sequence;
    }

    public Long getSequence() {
        return sequence;
    }

}
