package ee.sten.webshop;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PersonController {

    @Autowired
    PersonRepository personRepository;

    @GetMapping("persons")
    public List<Person> getPersons() {
        return personRepository.findAll();
    }

    @PostMapping("persons")
    public List<Person> addPersons(@RequestBody Person person) {
        if (!personRepository.existsById(person.getPersonCode())){
            personRepository.save(person);
        }
        return personRepository.findAll();
    }

  /*  @PutMapping("persons/{id}")
    public List<Person> editPersons(@RequestBody Person person, @PathVariable ) {
        if (!personRepository.existsById(person.getPersonCode())){
            personRepository.save(person);
        }
        return personRepository.findAll();
    }*/

   /* @DeleteMapping("persons")
    public List<Person> editPersons(@RequestBody Person person) {
        if (!personRepository.existsById(person.getPersonCode())){
            personRepository.save(person);
        }
        return personRepository.findAll();
    }*/
}
