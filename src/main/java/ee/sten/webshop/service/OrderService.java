package ee.sten.webshop.service;

import ee.sten.webshop.cache.ProductCache;
import ee.sten.webshop.controller.model.EveryPayData;
import ee.sten.webshop.controller.model.EveryPayResponse;
import ee.sten.webshop.entity.Order;
import ee.sten.webshop.entity.Person;
import ee.sten.webshop.entity.Product;
import ee.sten.webshop.repository.OrderRepository;
import ee.sten.webshop.repository.PersonRepository;
import ee.sten.webshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Locale;
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

    private String apiUsername = "92ddcfab96e34a5f";
    private String accountName = "EUR3D1";
    private String customerUrl = "https://sten-webshop.herokuapp.com/payment-completed";
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

    public Order saveOrder(Person person, List<Product> originalProducts, double totalSum) {
        Order order = new Order();
        order.setCreationDate(new Date());
        order.setPerson(person);
        order.setPaidState("initial");

        order.setProducts(originalProducts);

        order.setTotalSum(totalSum);
        return orderRepository.save(order);
    }

    public String getLinkFromEveryPay(Order order) {


        RestTemplate restTemplate = new RestTemplate();
        String url = "https://igw-demo.every-pay.com/api/v4/payments/oneoff";

        EveryPayData data = new EveryPayData();
        data.setApi_username(apiUsername);
        data.setAccount_name(accountName);
        data.setAmount(order.getTotalSum());
        data.setOrder_reference(order.getId().toString());
        data.setNonce(order.getId().toString() + new Date() + Math.random() );
        data.setCustomer_url(customerUrl);
        data.setTimestamp(ZonedDateTime.now().toString());


        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Basic OTJkZGNmYWI5NmUzNGE1Zjo4Y2QxOWU5OWU5YzJjMjA4ZWU1NjNhYmY3ZDBlNGRhZA==");

        HttpEntity<EveryPayData> entity = new HttpEntity<>(data, headers);
        ResponseEntity<EveryPayResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, EveryPayResponse.class);

        return response.getBody().payment_link;
    }
}
