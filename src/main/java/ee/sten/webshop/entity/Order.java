package ee.sten.webshop.entity;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@Data
@Entity
@SequenceGenerator(name="seq", initialValue = 100000, allocationSize = 1) //saab seadistada algkoha id-le, ehk antud juhul algab 100000st
@Table(name = "orders") //vahetame tabeli nime PostgreSQLis
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq")
    private  Long id;
    private Date creationDate;
    private double totalSum;
    @ManyToMany
    private List<Product> products;
    @ManyToOne
    private Person person;
}
