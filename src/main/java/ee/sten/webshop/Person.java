package ee.sten.webshop;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Person {
    @Id
    private String personCode;
    private String email;
    private String firstName;
    private String lastName;
    private String telephone;
    private String address;

}
