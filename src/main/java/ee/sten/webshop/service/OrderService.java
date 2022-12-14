package ee.sten.webshop.service;

import ee.sten.webshop.cache.ProductCache;
import ee.sten.webshop.controller.model.CartProduct;
import ee.sten.webshop.controller.model.EveryPayData;
import ee.sten.webshop.controller.model.EveryPayResponse;
import ee.sten.webshop.controller.model.EveryPayState;
import ee.sten.webshop.entity.Order;
import ee.sten.webshop.entity.Person;
import ee.sten.webshop.entity.Product;
import ee.sten.webshop.repository.CartProductRepository;
import ee.sten.webshop.repository.OrderRepository;
import ee.sten.webshop.repository.PersonRepository;
import ee.sten.webshop.repository.ProductRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Log4j2
@Service
public class OrderService {

    @Autowired
    OrderRepository orderRepository;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductCache productCache;

    @Autowired
    CartProductRepository cartProductRepository;

    @Autowired
    RestTemplate restTemplate; //lisab resttemplate annotatsiooni, kuna mul automaatne, peab uurima

    @Value("${everypay.username}")
    private String apiUsername;
    @Value("${everypay.account}")
    private String accountName;
    @Value("${everypay.customerurl}")
    private String customerUrl;
    @Value("${everypay.headers}")
    private String everyPayHeaders;

    @Value("${everypay.url}")
    private String everyPayUrl;

    public List<Product> findOriginalProducts(List<Long> products) {
        //otsi id alusel k??ikidele  toodetele originaal foriga:
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
                        return productCache.getProduct(e);
                    } catch (ExecutionException ex) {
                        throw new RuntimeException(ex);
                    }
                })
                .collect(Collectors.toList());
    }

    public double calculateTotalSum(List<CartProduct> cartProducts) {
        String personCode = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        log.info("Calculating total sum {}", personCode);
        //products.stream().mapToDouble(e -> e.getPrice());
        return cartProducts.stream()
                .mapToDouble(e -> e.getProduct().getPrice() * e.getQuantity())
                .sum();
    }

    @Transactional
    public Order saveOrder(Person person, List<CartProduct> cartProducts, double totalSum) {

        String personCode = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        log.info("Starting to save order {}", personCode);
        cartProductRepository.saveAll(cartProducts);

        Order order = new Order();
        order.setCreationDate(new Date());
        order.setPerson(person);
        order.setPaidState("initial");

        order.setLineItem(cartProducts);

        order.setTotalSum(totalSum);
        return orderRepository.save(order);
    }

    public EveryPayResponse getLinkFromEveryPay(Order order) {


        String url = everyPayUrl + "/payments/oneoff";

        EveryPayData data = new EveryPayData();
        data.setApi_username(apiUsername);
        data.setAccount_name(accountName);
        data.setAmount(order.getTotalSum());
        data.setOrder_reference(order.getId().toString());
        data.setNonce(order.getId().toString() + new Date() + Math.random() );
        data.setCustomer_url(customerUrl);
        data.setTimestamp(ZonedDateTime.now().toString());


        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", everyPayHeaders);

        HttpEntity<EveryPayData> entity = new HttpEntity<>(data, headers);
        ResponseEntity<EveryPayResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, EveryPayResponse.class);

        return response.getBody();
    }

    public String checkIfOrderIsPaid(String payment_reference) {
        ///TODO - teha p??rng EveryPaysse nagu tegime makse -> saata kaasa payment ref
        String url = everyPayUrl + "/payments/" + payment_reference + "?api_username=" + apiUsername;
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", everyPayHeaders);
        HttpEntity<String> httpEntity = new HttpEntity<>(headers);
        ResponseEntity<EveryPayState> response = restTemplate.exchange(url, HttpMethod.GET, httpEntity, EveryPayState.class);

        if(response.getBody() != null) {
            String order_reference = response.getBody().order_reference;
            Order order = orderRepository.findById(Long.parseLong(order_reference)).get();
            return getString(payment_reference, response, order_reference, order);
        } else {
            return "??henduse viga";
        }
    }

    private String getString(String payment_reference, ResponseEntity<EveryPayState> response, String order_reference, Order order) {
        switch (response.getBody().payment_state) {
            case "settled":
                order.setPaidState("settled");
                orderRepository.save(order);
                return "Makse ??nnestus: " + order_reference + payment_reference;
            case "failed":
                order.setPaidState("failed");
                orderRepository.save(order);
                return "Makse eba??nnestus: " + order_reference + payment_reference;
            case "cancelled":
                order.setPaidState("cancelled");
                orderRepository.save(order);
                return "Makse katkestati: " + order_reference + payment_reference;
            default:
                return "Makse ei toiminud";
        }
    }


}

//@controllerAdvice