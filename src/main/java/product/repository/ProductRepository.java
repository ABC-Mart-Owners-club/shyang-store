package product.repository;

import product.domain.Product;

import java.util.List;

public interface ProductRepository {

    public Product save(Product product);

    public List<Product> findByCodeOrderByReceiveDate(String code);

    public int countTotalStock(String code);
}
