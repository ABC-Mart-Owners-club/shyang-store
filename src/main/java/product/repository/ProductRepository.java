package product.repository;

import product.domain.Product;

public interface ProductRepository {

    public Product save(Product product);

    public Product findByCode(String code);
}
