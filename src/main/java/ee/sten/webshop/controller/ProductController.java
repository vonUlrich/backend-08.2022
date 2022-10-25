package ee.sten.webshop.controller;

import ee.sten.webshop.cache.ProductCache;
import ee.sten.webshop.controller.exceptions.CategoryInUseException;
import ee.sten.webshop.controller.exceptions.ProductInUseException;
import ee.sten.webshop.entity.Category;
import ee.sten.webshop.entity.Product;
import ee.sten.webshop.repository.CategoryRepository;
import ee.sten.webshop.repository.ProductRepository;
import ee.sten.webshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@RestController
public class ProductController {
  //  List<Product> products = new ArrayList<>();


    @Autowired
    ProductRepository productRepository;
    @Autowired
    ProductCache productCache;

    @Autowired
    CategoryRepository categoryRepository;

    @GetMapping("products")
        public ResponseEntity<List<Product>> getProducts() {
            return new ResponseEntity<>(productRepository.findAllByOrderById(), HttpStatus.OK);
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
//        products.add(product);
       // sout
//        System.out.println(!productRepository.findById(product.getId()).isPresent());
//        if (!productRepository.existsById(product.getId())) {
       productRepository.save(product);
//        }
       return new ResponseEntity<>(productRepository.findAllByOrderById(), HttpStatus.CREATED);
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
        return new ResponseEntity<>(productRepository.findAllByOrderById(), HttpStatus.OK);
    }

    /*@DeleteMapping("delete-product/{index}")
    private List<Product> deleteProduct(@PathVariable int index) {
        //  products.add(product);
        products.remove(index);
        return products;
    }*/
     @DeleteMapping("delete-product/{id}")
    public ResponseEntity<List<Product>> deleteProduct(@PathVariable Long id) throws ProductInUseException {
        //  products.add(product);
         try {
             productRepository.deleteById(id);
             productCache.emptyCache();
         } catch (DataIntegrityViolationException e) {
             throw new ProductInUseException();
         }

        return new ResponseEntity<>(productRepository.findAllByOrderById(), HttpStatus.OK);
    }

    // lisame igale tootele andmebaasi ka koguse  - entitys
    //API otspunkti kaudu saab kogusele +1 ja -1 panna
    //patch - mingi ühe omanduse asendamine
   /* @PatchMapping("add-stock")
    public ResponseEntity<List<Product>> addStock(@RequestBody Product product) {
         Product originalProduct = productRepository.findById(product.getId()).get();
         originalProduct.setStock(originalProduct.getStock()+1);
         productRepository.save(product);
        System.out.println("test --- " + product.getStock() );
         return  new ResponseEntity<>(productRepository.findAll(), HttpStatus.OK);

    }*/
    @PatchMapping("add-stock")
    public ResponseEntity<List<Product>> addStock(@RequestBody Product product) {
        Product originalProduct = productRepository.findById(product.getId()).get();
        originalProduct.setStock(originalProduct.getStock()+1);
        productRepository.save(originalProduct);
        return new ResponseEntity<>(productRepository.findAllByOrderById(), HttpStatus.OK);
    }

    @PatchMapping("decrease-stock")
    public ResponseEntity<List<Product>> decreaseStock(@RequestBody Product product) {
        Product originalProduct = productRepository.findById(product.getId()).get();
        if (originalProduct.getStock() > 0) {
            originalProduct.setStock(originalProduct.getStock()-1);
            productRepository.save(originalProduct);
        }
        return new ResponseEntity<>(productRepository.findAllByOrderById(), HttpStatus.OK);
    }

  /*  @PatchMapping("decrease-stock")
    public ResponseEntity<List<Product>> decreaseStock(@RequestBody Product product) {
        Product originalProduct = productRepository.findById(product.getId()).get();
        if(originalProduct.getStock() > 0) {
            originalProduct.setStock(originalProduct.getStock()-1);
            productRepository.save(product);
        }
     *//*   else {
            originalProduct.setStock(0);
            productRepository.save(product);
        }*//*

        return  new ResponseEntity<>(productRepository.findAll(), HttpStatus.OK);
    }*/

    @GetMapping("active-products")
    public ResponseEntity<List<Product>> getAllActiveProducts() {
        return new ResponseEntity<>(
                productRepository.findAllByStockGreaterThanAndActiveEqualsOrderByIdAsc(0,true),
                HttpStatus.OK
        );
    }

    @GetMapping("active-products/{pagenr}")
    public ResponseEntity<List<Product>> getActiveProductsPerPage(@PathVariable int pagenr) {
        PageRequest pageRequest = PageRequest.of(pagenr, 3);
        return new ResponseEntity<>(
                productRepository.findAllByStockGreaterThanAndActiveEqualsOrderByIdAsc(0,true, pageRequest),
                HttpStatus.OK
        );
    }

    @GetMapping("products-per-page/{pagenr}")
    public Page<Product> getProducsPerPage(@PathVariable int pagenr) {
        Pageable pageRequest = PageRequest.of(pagenr, 3);
        return productRepository.findAll(pageRequest);
    }

    @GetMapping("products-by-category/{categoryId}")
    public List<Long> getProductsPerPage(@PathVariable Long categoryId) {
        Category category = categoryRepository.findById(categoryId).get();
        List<Long> ids = productRepository.findAllByCategoryOrderByIdAsc(category).stream()
                .map(Product::getId)
                .collect(Collectors.toList());
        return ids;
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

    @Autowired
    OrderService orderService;
    @GetMapping("cart-products/{ids}")
    public List<Product> getOriginalProducts(@PathVariable List<Long> ids) {
        return orderService.findOriginalProducts(ids);
    }



}



