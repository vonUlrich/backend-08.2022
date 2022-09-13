package ee.sten.webshop.controller;

import ee.sten.webshop.controller.exceptions.CategoryInUseException;
import ee.sten.webshop.entity.Category;
import ee.sten.webshop.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@CrossOrigin("http://localhost:3000")
public class CategoryController {

    @Autowired
    CategoryRepository categoryRepository;

    @GetMapping("category")
    public ResponseEntity<List<Category>> getCategories() {

        return new ResponseEntity<>(categoryRepository.findAll(), HttpStatus.OK);
    }

    @PostMapping("category")
    public ResponseEntity<List<Category>> addCategory(@RequestBody Category category) {
        categoryRepository.save(category);
        return new ResponseEntity<>(categoryRepository.findAll(), HttpStatus.CREATED);
    }

    @DeleteMapping("category/{id}")
    public ResponseEntity<List<Category>> deleteCategory(@PathVariable Long id) throws CategoryInUseException{
        try {
            categoryRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
           throw new CategoryInUseException();
        }
        return  new ResponseEntity<>(categoryRepository.findAll(), HttpStatus.OK);
    }
}
