package ee.sten.webshop.controller;

import ee.sten.webshop.controller.model.EveryPayState;
import ee.sten.webshop.service.OrderService;
import ee.sten.webshop.entity.Order;
import ee.sten.webshop.entity.Person;
import ee.sten.webshop.entity.Product;
import ee.sten.webshop.repository.OrderRepository;
import ee.sten.webshop.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.websocket.server.PathParam;
import java.util.List;

@RestController
public class OrderController {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    PersonRepository personRepository;
    @Autowired
    OrderService orderService;

    @GetMapping("orders/{personCode}")
    public ResponseEntity<List<Order>> getPersonOrders(@PathVariable String personCode) {
        Person person = personRepository.findById(personCode).get();
        return new ResponseEntity<>(orderRepository.findAllByPerson(person), HttpStatus.OK);
    }

    @PostMapping("orders/{personCode}")
    public ResponseEntity<String> addNewOrder(@PathVariable String personCode, @RequestBody List<Product> products) {

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

}
