package ee.sten.webshop;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.Id;

@Data
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
@Entity
public class Product {
    @Id
    private Long id;
    private String name;
    private double price;
    private String image;
    private boolean active;
}
