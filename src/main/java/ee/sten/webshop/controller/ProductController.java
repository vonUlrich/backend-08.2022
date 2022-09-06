package ee.sten.webshop.controller;

import ee.sten.webshop.cache.ProductCache;
import ee.sten.webshop.entity.Product;
import ee.sten.webshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@CrossOrigin("http://localhost:3000")
public class ProductController {
  //  List<Product> products = new ArrayList<>();


    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductCache productCache;

    @GetMapping("products")
        public ResponseEntity<List<Product>> getProducts() {
            return new ResponseEntity<>(productRepository.findAll(), HttpStatus.OK);
        }

    @GetMapping("get-product/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable Long id) throws ExecutionException {
        //return productRepository.findById(id).get();
        return new ResponseEntity<>(productCache.getProduct(id), HttpStatus.OK);
    }


   // @GetMapping("products?id={id}&name={name}&price={price}&image=image&active={active}")
 /*   @GetMapping("add-product")
    private List<Product> addProducts(
            @PathParam("id") Long id,
            @PathParam("name") String name,
            @PathParam("price") double price,
            @PathParam("image") String image,
            @PathParam("active") boolean active) {

    }*/
    @PostMapping("add-product")
    public ResponseEntity<List<Product>> addProduct(@RequestBody Product product) {
      //  if (!productRepository.existsById(product.getId())){
            productRepository.save(product);
    //    }
        return new ResponseEntity<>(productRepository.findAll(), HttpStatus.CREATED);
    }

  /*  @PutMapping("edit-product/{index}")
    private List<Product> editProduct(@RequestBody Product product, @PathVariable int index) {
      //  products.add(product);
        products.set(index, product);
        return productRepository.findAll();
    }*/

    @PutMapping("edit-product/{index}")
    public ResponseEntity<List<Product>> editProduct(@RequestBody Product product, @PathVariable int index) {
      //  products.add(product);
    if (productRepository.existsById(product.getId())){
        productRepository.save(product);
        productCache.emptyCache();
    }
        return new ResponseEntity<>(productRepository.findAll(), HttpStatus.OK);
    }

    /*@DeleteMapping("delete-product/{index}")
    private List<Product> deleteProduct(@PathVariable int index) {
        //  products.add(product);
        products.remove(index);
        return products;
    }*/
     @DeleteMapping("delete-product/{id}")
    public ResponseEntity<List<Product>> deleteProduct(@PathVariable Long id) {
        //  products.add(product);
        productRepository.deleteById(id);
        productCache.emptyCache();
        return new ResponseEntity<>(productRepository.findAll(), HttpStatus.OK);
    }

    // lisame igale tootele andmebaasi ka koguse  - entitys
    //API otspunkti kaudu saab kogusele +1 ja -1 panna
    //patch - mingi ühe omanduse asendamine
    @PatchMapping("add-stock")
    public ResponseEntity<List<Product>> addStock(@RequestBody Product product) {
         Product originalProduct = productRepository.findById(product.getId()).get();
         originalProduct.setStock(originalProduct.getStock()+1);
         productRepository.save(product);
         return  new ResponseEntity<>(productRepository.findAll(), HttpStatus.OK);
    }

    @PatchMapping("decrease-stock")
    public ResponseEntity<List<Product>> decreaseStock(@RequestBody Product product) {
        Product originalProduct = productRepository.findById(product.getId()).get();
        if(originalProduct.getStock() > 0) {
            originalProduct.setStock(originalProduct.getStock()-1);
            productRepository.save(product);
        }
     /*   else {
            originalProduct.setStock(0);
            productRepository.save(product);
        }*/

        return  new ResponseEntity<>(productRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("active-products")
    public ResponseEntity<List<Product>> getAllActiveProducts() {
        return new ResponseEntity<>(
                productRepository.findAllByStockGreaterThanAndActiveEqualsOrderByIdAsc(0,true),
                HttpStatus.OK
        );
    }

   /* @GetMapping("active-products/{pagenr}")
    public ResponseEntity<List<Product>> getActiveProductsPerPage(@PathVariable int pagenr) {
        PageRequest pageRequest = PageRequest.of(pagenr, 3);
        return new ResponseEntity<>(
                productRepository.findAllByStockGreaterThanAndActiveEqualsOrderByIdAsc(0,true, pageRequest),
                HttpStatus.OK
        );
    }*/

    @GetMapping("products-per-page/{pagenr}")
    public Page<Product> getProducsPerPage(@PathVariable int pagenr) {
        Pageable pageRequest = PageRequest.of(pagenr, 3);
        return productRepository.findAll(pageRequest);
    }

    //-1 kaudu ei lase miinusesse

    //eraldi teha API otspunkti aktiivsete  ja + koguste jaoks

    // Pagination

    // Productis kontrollid, et ei saa ilma nime ja hinnata sisestada

/*    @DeleteMapping("delete-product-id/{id}")
    private List<Product> deleteProductById(@PathVariable Long id) {
        if (products.stream().anyMatch(e -> e.getId().equals(id))) {
            Product product = products.stream().filter(e -> e.getId().equals(id)).findFirst().get();
            products.remove(id);
        }
        return products;
    }*/

}



