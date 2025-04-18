package repository;

import domain.Product;

import java.util.HashMap;
import java.util.Map;

public class ProductRepository {

    private Long sequence = 2L;
    static Map<Long, Product> productMap = new HashMap<>(Map.of(
            0L, new Product(0L, "Nike Air Max", 100_000, 500),
            1L, new Product(1L, "Adidas Ultraboost", 120_000, 200),
            2L, new Product(2L, "Puma RS-X", 90_000, 60)
    ));

    public Product findById(Long id) {
        return productMap.get(id);
    }

    public Long save(String name, int price, int stockQuantity) {
        sequence ++;
        Product product = new Product(sequence, name, price, stockQuantity);
        productMap.put(sequence, product);
        return sequence;
    }

    public Product findByName(String name) {
        return productMap.values().stream()
                .filter(product -> product.getName().equals(name))
                .findFirst()
                .orElse(null);
    }

    public Long getSequence() {
        return sequence;
    }
}
