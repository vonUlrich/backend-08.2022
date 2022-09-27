package ee.sten.webshop.controller;

import ee.sten.webshop.controller.model.EveryPayResponse;
import ee.sten.webshop.controller.model.EveryPayState;
import ee.sten.webshop.entity.Category;
import ee.sten.webshop.repository.ProductRepository;
import ee.sten.webshop.service.OrderService;
import ee.sten.webshop.entity.Order;
import ee.sten.webshop.entity.Person;
import ee.sten.webshop.entity.Product;
import ee.sten.webshop.repository.OrderRepository;
import ee.sten.webshop.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.websocket.server.PathParam;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class OrderController {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    PersonRepository personRepository;
    @Autowired
    OrderService orderService;

    @Autowired
    ProductRepository productRepository;

    @GetMapping("orders")
    public ResponseEntity<List<Order>> getPersonOrders() {
        String personCode = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Person person = personRepository.findById(personCode).get();
        return new ResponseEntity<>(orderRepository.findAllByPerson(person), HttpStatus.OK);
    }

    @PostMapping("orders")
    public ResponseEntity<EveryPayResponse> addNewOrder( @RequestBody List<Product> products) {
        String personCode = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        List<Product> originalProducts = orderService.findOriginalProducts(products);

        double totalSum = orderService.calculateTotalSum(originalProducts);

        Person person = personRepository.findById(personCode).get();
        Order order = orderService.saveOrder(person, originalProducts, totalSum);

        return new ResponseEntity<>(orderService.getLinkFromEveryPay(order), HttpStatus.OK);
    }

    @GetMapping("payment-completed")
    public ResponseEntity<String> checkIfPaid(
                              @PathParam("payment_reference") String payment_reference) {

       return new ResponseEntity<>(orderService.checkIfOrderIsPaid(payment_reference), HttpStatus.OK);
    }

    @GetMapping("orders-by-product/{productId}")
    public List<Long> getOrdersByProduct(@PathVariable Long productId) {
        Product product = productRepository.findById(productId).get();
        List<Long> ids = orderRepository.findAllByProductsOrderByIdAsc(product).stream()
                .map(Order::getId)
                .collect(Collectors.toList());
        return ids;
    }

}
