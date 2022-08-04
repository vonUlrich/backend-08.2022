package ee.sten.webshop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.websocket.server.PathParam;
import java.util.ArrayList;
import java.util.List;

@RestController
public class ProductController {
  //  List<Product> products = new ArrayList<>();


    @Autowired
    ProductRepository productRepository;

    @GetMapping("products")
        public List<Product> getProducts() {
            return productRepository.findAll();
        }

    @GetMapping("get-product/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productRepository.findById(id).get();
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
        if (!productRepository.existsById(product.getId())){
            productRepository.save(product);
        }
        return productRepository.findAll();
    }

  /*  @PutMapping("edit-product/{index}")
    public List<Product> editProduct(@RequestBody Product product, @PathVariable int index) {
      //  products.add(product);
        products.set(index, product);
        return productRepository.findAll();
    }*/

    @PutMapping("edit-product/{index}")
    public List<Product> editProduct(@RequestBody Product product, @PathVariable int index) {
      //  products.add(product);
    if (productRepository.existsById(product.getId())){
        productRepository.save(product);
    }
        return productRepository.findAll();
    }

    /*@DeleteMapping("delete-product/{index}")
    public List<Product> deleteProduct(@PathVariable int index) {
        //  products.add(product);
        products.remove(index);
        return products;
    }*/
     @DeleteMapping("delete-product/{id}")
    public List<Product> deleteProduct(@PathVariable Long id) {
        //  products.add(product);
        productRepository.deleteById(id);
        return productRepository.findAll();
    }

/*    @DeleteMapping("delete-product-id/{id}")
    public List<Product> deleteProductById(@PathVariable Long id) {
        if (products.stream().anyMatch(e -> e.getId().equals(id))) {
            Product product = products.stream().filter(e -> e.getId().equals(id)).findFirst().get();
            products.remove(id);
        }
        return products;
    }*/

}



