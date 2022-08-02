package ee.sten.webshop;

import lombok.*;

@Data
//@Getter
//@Setter
//@NoArgsConstructor
//@AllArgsConstructor
public class Product {
    private Long id;
    private String name;
    private double price;
    private String image;
    private boolean active;
}
