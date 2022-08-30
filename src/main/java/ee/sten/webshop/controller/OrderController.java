package ee.sten.webshop.controller;

import ee.sten.webshop.controller.model.EveryPayState;
import ee.sten.webshop.service.OrderService;
import ee.sten.webshop.entity.Order;
import ee.sten.webshop.entity.Person;
import ee.sten.webshop.entity.Product;
import ee.sten.webshop.repository.OrderRepository;
import ee.sten.webshop.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
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
    public List<Order> getPersonOrders(@PathVariable String personCode) {
        Person person = personRepository.findById(personCode).get();
        return orderRepository.findAllByPerson(person);
    }

    @PostMapping("orders/{personCode}")
    public String addNewOrder(@PathVariable String personCode, @RequestBody List<Product> products) {

        List<Product> originalProducts = orderService.findOriginalProducts(products);

        double totalSum = orderService.calculateTotalSum(originalProducts);

        Person person = personRepository.findById(personCode).get();
        Order order = orderService.saveOrder(person, originalProducts, totalSum);

        return orderService.getLinkFromEveryPay(order);
    }

    @GetMapping("payment-completed")
    public String checkIfPaid(
            //@PathParam("order_reference") String order_reference,
                              @PathParam("payment_reference") String payment_reference) {



        ///TODO - teha pärng EveryPaysse nagu tegime makse -> saata kaasa payment ref
        String url = "https://igw-demo.every-pay.com/api/v4/payments/" + payment_reference + "?api_username=92ddcfab96e34a5f";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic OTJkZGNmYWI5NmUzNGE1Zjo4Y2QxOWU5OWU5YzJjMjA4ZWU1NjNhYmY3ZDBlNGRhZA==");
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<EveryPayState> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, EveryPayState.class);

        if(response.getBody() != null) {
            String order_reference = response.getBody().order_reference;
            Order order = orderRepository.findById(Long.parseLong(order_reference)).get();
            switch (response.getBody().payment_state) {
                case "settled":
                    order.setPaidState("settled");
                    return "Makse õnnestus: " + order_reference + payment_reference;
                case "failed":
                    order.setPaidState("failed");
                    return "Makse ebaõnnestus: " + order_reference + payment_reference;
                case "cancelled":
                    order.setPaidState("cancelled");
                    return "Makse katkestati: " + order_reference + payment_reference;
                default:
                    return "Makse ei toiminud";
            }
        } else {
            return "Ühenduse viga";
        }
    }

}
