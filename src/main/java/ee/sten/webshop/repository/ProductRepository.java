package ee.sten.webshop.repository;

import ee.sten.webshop.entity.Product;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findAllByStockGreaterThanAndActiveEqualsOrderByIdAsc(int stock, boolean active);

    List<Product> findAllByStockGreaterThanAndActiveEqualsOrderByIdAsc(int stock, boolean active, Pageable pageable);
}
