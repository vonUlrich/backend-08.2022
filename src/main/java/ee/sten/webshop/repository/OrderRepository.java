package ee.sten.webshop.repository;


import ee.sten.webshop.entity.Order;
import ee.sten.webshop.entity.Person;
import ee.sten.webshop.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findAllByPerson(Person person);

   // List<Order> findAllByProductsOrderByIdAsc(Product product);
}
