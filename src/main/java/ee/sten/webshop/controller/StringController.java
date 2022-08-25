package ee.sten.webshop.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
public class StringController {

    List<String> strings = new ArrayList<>(Arrays.asList("Audi", "BMW", "VW", "Honda", "Jeep"));

    @GetMapping("strings")
    public List<String> getStrings() {
        return strings;
    }

    @GetMapping("strings/{newString}")
    public List<String> addStrings(@PathVariable String newString) {
        strings.add(newString);
        return strings;
    }

    @GetMapping("hi")
    public String sayHello() {
        return "Hello";
    }

    //http://localhost:8080/hi/sten
    @GetMapping("hi/{personName}")
    public String sayHelloToName(@PathVariable String personName) {
        return "Hello, " + personName;
    }

    @GetMapping("calculate/{nr1}/{nr2}")
    public Integer sayHelloToName(@PathVariable int nr1, @PathVariable int nr2 ) {
        return nr1+nr2;
    }

}
