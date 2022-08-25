package ee.sten.webshop.service;

import ee.sten.webshop.cache.ProductCache;
import ee.sten.webshop.entity.Order;
import ee.sten.webshop.entity.Person;
import ee.sten.webshop.entity.Product;
import ee.sten.webshop.repository.OrderRepository;
import ee.sten.webshop.repository.PersonRepository;
import ee.sten.webshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductCache productCache;
    public List<Product> findOriginalProducts(List<Product> products) {
        //otsi id alusel kõikidele  toodetele originaal foriga:
      /*  List<Product> originalProducts = new ArrayList<>();
        for (Product product : products) {
            Long id = product.getId();
            productRepository.findById(id);
            Product originalProduct = productRepository.findById(id).get();
            originalProducts.add(originalProduct);
        }*/
        //vs streamiga:
        return products.stream()
                //.map(e -> productRepository.findById(e.getId()).get())
                .map(e -> {
                    try {
                        return productCache.getProduct(e.getId());
                    } catch (ExecutionException ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .collect(Collectors.toList());
    }

    public double calculateTotalSum(List<Product> originalProducts) {
        //products.stream().mapToDouble(e -> e.getPrice());
        return originalProducts.stream()
                .mapToDouble(Product::getPrice)
                .sum();
    }

    public void saveOrder(Person person, List<Product> originalProducts, double totalSum) {
        Order order = new Order();
        order.setCreationDate(new Date());
        order.setPerson(person);

        order.setProducts(originalProducts);

        order.setTotalSum(totalSum);
        orderRepository.save(order);
    }
}
