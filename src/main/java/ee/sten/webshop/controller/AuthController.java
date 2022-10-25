package ee.sten.webshop.controller;

import ee.sten.webshop.auth.TokenGenerator;
import ee.sten.webshop.controller.exceptions.WrongPasswordException;
import ee.sten.webshop.controller.model.AuthResponse;
import ee.sten.webshop.controller.model.LoginData;
import ee.sten.webshop.controller.model.TokenResponse;
import ee.sten.webshop.entity.Person;
import ee.sten.webshop.repository.PersonRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {

    @Autowired
    PersonRepository personRepository;

    @Autowired
    BCryptPasswordEncoder passwordEncoder;

    @Autowired
    TokenGenerator tokenGenerator;

    @PostMapping("signup")
    public ResponseEntity<String> signup(@RequestBody Person person) {
        if (!personRepository.existsById(person.getPersonCode())) {
            if(personRepository.findPersonByEmail(person.getEmail()) == null){
                String password = person.getPassword();
//                BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
                person.setPassword(passwordEncoder.encode(password));
                personRepository.save(person);
            } else {
                return new ResponseEntity<>("Sellise emailiga kasutaja on juba olemas", HttpStatus.BAD_REQUEST);
            }
        } else {
            return new ResponseEntity<>("Sellise isikukoodiga kasutaja on juba olemas", HttpStatus.BAD_REQUEST);
        }
        return new ResponseEntity<>("Edukalt registreeritud", HttpStatus.CREATED);
    }

    ///TODO login
    @PostMapping("login")
    public ResponseEntity<TokenResponse> login(@RequestBody LoginData loginData) throws WrongPasswordException {
        String email = loginData.getEmail();
        Person person = personRepository.findPersonByEmail(email);
        if (passwordEncoder.matches(loginData.getPassword(),person.getPassword())) {
            // genereeri uus token ja tagasta see kasutajale
//            String token = "adsadsa";
            TokenResponse tokenResponse = tokenGenerator.generateNewToken(person);
//            tokenResponse.setToken(token);
            return new ResponseEntity<>(tokenResponse, HttpStatus.OK);
        }
        else {
            // parem klõps -> refactor -> rename
            throw new WrongPasswordException();
        }
    }

    @GetMapping("check-if-admin")
    public ResponseEntity<AuthResponse> checkIfAdmin() {
       /* String personCode = SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString();
        Person person = personRepository.findById(personCode).get();
        if ()*/
        return new ResponseEntity<>(new AuthResponse(true), HttpStatus.OK);
    }

    @GetMapping("check-if-logged-in")
    public ResponseEntity<AuthResponse> checkIfLoggedIn() {
        return new ResponseEntity<>(new AuthResponse(true), HttpStatus.OK);
    }
}
