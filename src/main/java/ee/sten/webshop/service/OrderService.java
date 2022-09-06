package ee.sten.webshop.service;

import ee.sten.webshop.cache.ProductCache;
import ee.sten.webshop.controller.model.EveryPayData;
import ee.sten.webshop.controller.model.EveryPayResponse;
import ee.sten.webshop.controller.model.EveryPayState;
import ee.sten.webshop.entity.Order;
import ee.sten.webshop.entity.Person;
import ee.sten.webshop.entity.Product;
import ee.sten.webshop.repository.OrderRepository;
import ee.sten.webshop.repository.PersonRepository;
import ee.sten.webshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.ZonedDateTime;
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

        return response.getBody().payment_link;
    }

    public String checkIfOrderIsPaid(String payment_reference) {
        ///TODO - teha pärng EveryPaysse nagu tegime makse -> saata kaasa payment ref
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
            return "Ühenduse viga";
        }
    }

    private String getString(String payment_reference, ResponseEntity<EveryPayState> response, String order_reference, Order order) {
        switch (response.getBody().payment_state) {
            case "settled":
                order.setPaidState("settled");
                orderRepository.save(order);
                return "Makse õnnestus: " + order_reference + payment_reference;
            case "failed":
                order.setPaidState("failed");
                orderRepository.save(order);
                return "Makse ebaõnnestus: " + order_reference + payment_reference;
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