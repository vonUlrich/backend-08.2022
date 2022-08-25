package ee.sten.webshop.controller;

import com.sun.xml.bind.v2.TODO;
import ee.sten.webshop.entity.Person;
import ee.sten.webshop.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AuthController {

    @Autowired
    PersonRepository personRepository;

    @PostMapping("signup")
    public String signup(@RequestBody Person person) {
        if (!personRepository.existsById(person.getPersonCode())) {
            personRepository.save(person);
        }
        return "Edukalt registreeritud";
    }

    ///TODO login

}
