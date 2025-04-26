package repository;

import domain.Product;

public interface ProductRepository {

    public Product save(String code, String name, int price, int stockQuantity);

    public Product findByCode(String code);
}
