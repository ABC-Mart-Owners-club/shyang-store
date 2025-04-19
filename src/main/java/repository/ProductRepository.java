package repository;

import domain.Product;

import java.util.HashMap;
import java.util.Map;

public class ProductRepository {

    static Map<String, Product> productMap = new HashMap<>(Map.of(
            "CODE-1", new Product("CODE-1", "Nike Air Max", 100_000, 500),
            "CODE-2", new Product("CODE-2", "Adidas Ultraboost", 120_000, 200),
            "CODE-3", new Product("CODE-3", "Puma RS-X", 90_000, 60)
    ));


    public Product save(String code, String name, int price, int stockQuantity) {
        Product product = new Product(code, name, price, stockQuantity);
        productMap.put(code, product);
        return product;
    }

    public Product findByCode(String code) {
        return productMap.get(code);
    }
}
