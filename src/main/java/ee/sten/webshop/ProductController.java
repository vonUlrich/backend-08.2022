package ee.sten.webshop;

import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductController {
    List<Product> products = new ArrayList<>();

    @GetMapping("products")
        public List<Product> getProducts() {
            return products;
        }

   // @GetMapping("products?id={id}&name={name}&price={price}&image=image&active={active}")
 /*   @GetMapping("add-product")
    public List<Product> addProducts(
            @PathParam("id") Long id,
            @PathParam("name") String name,
            @PathParam("price") double price,
            @PathParam("image") String image,
            @PathParam("active") boolean active) {

    }*/
    @PostMapping("add-product")
    public List<Product> addProduct(@RequestBody Product product) {
        products.add(product);
        return products;
    }

}



