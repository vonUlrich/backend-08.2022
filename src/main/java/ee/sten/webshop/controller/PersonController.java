package ee.sten.webshop.controller;

import ee.sten.webshop.entity.Person;
import ee.sten.webshop.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PersonController {

    @Autowired
    PersonRepository personRepository;

    @GetMapping("persons")
    private List<Person> getPersons() {
        return personRepository.findAll();
    }

 /*   @PostMapping("persons")
    private List<Person> addPersons(@RequestBody Person person) {
        if (!personRepository.existsById(person.getPersonCode())){
            personRepository.save(person);
        }
        return personRepository.findAll();
    }*/

  /*  @PutMapping("persons/{id}")
    private List<Person> editPersons(@RequestBody Person person, @PathVariable ) {
        if (!personRepository.existsById(person.getPersonCode())){
            personRepository.save(person);
        }
        return personRepository.findAll();
    }*/

   /* @DeleteMapping("persons")
    private List<Person> editPersons(@RequestBody Person person) {
        if (!personRepository.existsById(person.getPersonCode())){
            personRepository.save(person);
        }
        return personRepository.findAll();
    }*/
}
